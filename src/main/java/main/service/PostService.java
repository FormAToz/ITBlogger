package main.service;

import main.Main;
import main.api.response.PostCountResponse;
import main.api.response.PostResponse;
import main.api.response.ResultResponse;
import main.api.response.UserIdNameResponse;
import main.model.Post;
import main.model.Tag;
import main.model.User;
import main.repository.PostRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TagService tagService;
    @Autowired
    private TextService textService;
    @Autowired
    private TimeService timeService;

    /**
     * Создание тестовых постов
     */
    private void createTestPosts() {
        for (int i = 0; i < 100; i++) {
            Post p = new Post();
            p.setTitle("TestPost " + i);
            p.setText("TestPost " + i + " TestPostTestPostTestPostTestPostTestPostTestPostTestPostTestPostTestPostTestPost");
            p.setActive(1);
            p.setTime(LocalDateTime.now().plusHours(3));
            p.setModerationStatus(Post.ModerationStatus.ACCEPTED);
            p.setUser(userService.getUserById(1));
            p.setViewCount(0);
            postRepository.save(p);
        }
    }

    /**
     * Список постов - GET /api/post/
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести (10)
     * @param mode   - режим вывода (сортировка):
     *               recent - сортировать по дате публикации, выводить сначала новые
     *               popular - сортировать по убыванию количества комментариев
     *               best - сортировать по убыванию количества лайков
     *               early - сортировать по дате публикации, выводить сначала старые
     */
    public ResponseEntity<PostCountResponse> getPosts(int offset, int limit, String mode) {
        List<Post> filteredPosts = sortFilteredPosts(postRepository.findAllFilteredPosts(), mode);
        PostCountResponse postsCount = countPosts(filteredPosts, offset, limit);
        return new ResponseEntity<>(postsCount, HttpStatus.OK);
    }

    /**
     * Добавление поста - POST /api/post
     */
    public ResponseEntity<ResultResponse> addPost(Post post) {
        User user = userService.getUserFromSession();

        if (textService.titleAndTextIsShort(post.getTitle(), post.getText())) {
            return textService.checkTitleAndTextLength(post.getTitle(), post.getText());
        }
        if (user == null) {
            LOGGER.info(MARKER, "Пользователь не зарегистрирован в сессии!");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        post.setUser(user);
        post.setTags(tagService.checkDuplicatesInRepo(post.getTags()));
        post.setTime(timeService.getExpectedTime(post.getTime()));
        post.setModerationStatus(Post.ModerationStatus.NEW);
        post.setViewCount(0);
        postRepository.save(post);
        LOGGER.info(MARKER, "Пост добавлен. Id: {}, title: {}. User Id: {}, name: {}",
                post.getId(), post.getTitle(), post.getUser().getId(), post.getUser().getName());
        return new ResponseEntity<>(new ResultResponse(true, null), HttpStatus.OK);
    }

    /**
     * Поиск постов - GET /api/post/search/
     * Метод возвращает посты, соответствующие поисковому запросу - строке query.
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести
     * @param query  - поисковый запрос
     */
    public ResponseEntity<PostCountResponse> searchPosts(int offset, int limit, String query) {
        List<Post> foundedPosts = postRepository.findPostsByQuery(query);
        PostCountResponse postCountResponse = countPosts(foundedPosts, offset, limit);
        return new ResponseEntity<>(postCountResponse, HttpStatus.OK);
    }

    /**
     * Получение поста - GET /api/post/{ID}
     * Метод выводит данные конкретного поста для отображения на странице поста, в том числе,
     * список комментариев и тэгов, привязанных к данному посту.
     * @param id - id поста
     */
    public ResponseEntity<PostResponse> getPostById(int id) {
        PostResponse postResponse;
        User user = userService.getUserFromSession();
        Post post = postRepository.getPostById(id);

        if (user == null) {
            LOGGER.info(MARKER, "Пользователь не зарегистрирован в сессии!");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        if (post == null) {
            LOGGER.info(MARKER, "Запрашиваемый пост с id = {} не существует", id);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        postResponse = migrateToPostResponse(post);
        // TODO разобраться со значением
        // должно быть значение true если пост опубликован и false если скрыт (при этом модераторы и автор поста будет его видеть)
        postResponse.setActive(true);
        postResponse.setText(post.getText());
        // TODO проверить отображение
        postResponse.setComments(post.getComments());
        postResponse.setTags(post.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList()));
        // Пользователь не модератор и не автор
        // TODO реализовать сервис инкремента просмотров
        if (!userService.isModerator(user) && !userService.isAuthor(user, post)) {
            int viewCount = post.getViewCount();
            postResponse.setViewCount(++viewCount);
            post.setViewCount(viewCount);
            postRepository.save(post);
        }
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    /**
     * Список постов за указанную дату - GET /api/post/byDate
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести (10)
     * @param date   - дата в формате "2019-10-15"
     */
    public ResponseEntity<PostCountResponse> getPostsByDate(int offset, int limit, String date) {
        // TODO проверить дату и реализовать вывод по указанной дате
        List<Post> filteredPosts = postRepository.findAllFilteredPosts();
        PostCountResponse postsCount = countPosts(filteredPosts, offset, limit);
        return new ResponseEntity<>(postsCount, HttpStatus.OK);
    }

    /**
     * Список постов по тэгу - GET /api/post/byTag
     * Метод выводит список постов, привязанных к тэгу, который был передан методу в качестве параметра tag.
     * @param offset  - сдвиг от 0 для постраничного вывода
     * @param limit   - количество постов, которое надо вывести (10)
     * @param tagName - тэг, по которому нужно вывести все посты
     */
    public ResponseEntity<PostCountResponse> getPostsByTag(int offset, int limit, String tagName) {
        List<Post> allPostsByTag = postRepository.findFilteredPostsByTag(tagName);
        PostCountResponse postsCount = countPosts(allPostsByTag, offset, limit);
        return new ResponseEntity<>(postsCount, HttpStatus.OK);
    }

    /**
     * Выбор сортировки отображаемых постов
     */
    private List<Post> sortFilteredPosts(List<Post> list, String mode) {
        Comparator<Post> postComparator = null;

        // Сортировать по дате публикации, выводить сначала новые
        if (mode.equals("recent")) {
            postComparator = Comparator.comparing(Post::getTime).reversed();
        }
        // Сортировать по убыванию количества комментариев
        if (mode.equals("popular")) {
            // TODO реализовать сортирвку по убыванию количества комментариев
        }
        // Сортировать по убыванию количества лайков
        if (mode.equals("best")) {
            // TODO реализовать сортирвку по убыванию количества лайков
        }
        // Сортировать по дате публикации, выводить сначала старые
        if (mode.equals("early")) {
            postComparator = Comparator.comparing(Post::getTime);
        }
        return list.stream().sorted(postComparator).collect(Collectors.toList());
    }

    /**
     * Преобразование к количеству и списку постов для отображения
     */
    private PostCountResponse countPosts(List<Post> list, int offset, int limit) {
        List<PostResponse> postsList = new ArrayList<>();
        int count = list.size();        // кол-во постов

        list.stream()
                .skip(offset)
                .limit(limit)
                .forEach(post -> {
                    PostResponse postResponse = migrateToPostResponse(post);
                    postsList.add(postResponse);
                });
        return new PostCountResponse(count, postsList);
    }

    private PostResponse migrateToPostResponse(Post post) {
        PostResponse postResponse = new PostResponse();
        User author = post.getUser();

        postResponse.setId(post.getId());
        postResponse.setTime(timeService.timeToString(post.getTime()));
        postResponse.setUser(new UserIdNameResponse(author.getId(), author.getName()));
        postResponse.setTitle(post.getTitle());
        postResponse.setAnnounce(textService.getAnnounce(post.getText()));
        postResponse.setLikeCount(0);      // TODO реализовать
        postResponse.setDislikeCount(0);   // TODO реализовать
        postResponse.setCommentCount(0);   // TODO реализовать
        postResponse.setViewCount(post.getViewCount());
        return postResponse;
    }
}