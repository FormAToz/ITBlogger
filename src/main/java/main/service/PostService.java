package main.service;

import main.Main;
import main.api.response.*;
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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");
    private final int MIN_ANNOUNCE_TEXT_LENGTH = 40;
    private final int MIN_TITLE_LENGTH = 3;
    private final int MIN_TEXT_LENGTH = 50;
    @Autowired
    private HttpServletRequest servletRequest;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TagService tagService;

    // Создание тестовых постов
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
        // Получаем и сортируем все отфильтрованные посты
        List<Post> filteredPosts = sortFilteredPosts(postRepository.findAllFilteredPosts(), mode);
        PostCountResponse postsCount = countPosts(filteredPosts, offset, limit);
        return new ResponseEntity<>(postsCount, HttpStatus.OK);
    }

    /**
     * Добавление поста - POST /api/post
     */
    public ResponseEntity<ResultResponse> addPost(Post post) {
        Map<String, String> errors = new HashMap<>();
        ArrayList<Tag> tags = (ArrayList<Tag>) tagService.checkDuplicatesInRepo(post.getTags());
        String sessionId = servletRequest.getSession().getId();
        User user = userService.getUserFromSession(sessionId);
        // Проверка длины заголовка и текста
        if (post.getTitle().length() < MIN_TITLE_LENGTH) {
            errors.put("title", "Заголовок публикации менее " + MIN_TITLE_LENGTH + " символов");
        }
        if (post.getText().length() < MIN_TEXT_LENGTH) {
            errors.put("text", "Текст публикации менее " + MIN_TEXT_LENGTH + " символов");
        }
        // Проверка пользователя
        if (user == null) {
            errors.put("user", "Пользователь не зарегистрирован!");
        }else {
            post.setUser(user);
        }
        if (!errors.isEmpty()) {
            return new ResponseEntity<>(new ResultResponse(false, errors), HttpStatus.BAD_REQUEST);
        }
        post.setTags(tags);
        // Проверка времени публикации
        if (post.getTime().isBefore(LocalDateTime.now())) {
            post.setTime(LocalDateTime.now());
        }
        post.setTime(post.getTime().plusHours(3));
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
        // Находим все отфильтрованные посты по запросу
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
        String sessionId = servletRequest.getSession().getId();
        User user = userService.getUserFromSession(sessionId);
        Post post = postRepository.getPostById(id);
        if (post == null) {
            LOGGER.info(MARKER, "Запрашиваемый пост с id = {} не существует", id);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        PostResponse postResponse = migrateToPostResponse(post);
        List<String> tags = post.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        if (user == null) {
            LOGGER.info(MARKER, "Пользователь не зарегистрирован!");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        // TODO разобраться со значением
        // должно быть значение true если пост опубликован и false если скрыт (при этом модераторы и автор поста будет его видеть)
        postResponse.setActive(true);
        postResponse.setText(post.getText());
        // TODO проверить отображение
        postResponse.setComments(post.getComments());
        postResponse.setTags(tags);
        // Пользователь не модератор и не автор
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
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести (10)
     * @param tag    - тэг, по которому нужно вывести все посты
     */
    public ResponseEntity<PostCountResponse> getPostsByTag(int offset, int limit, String tag) {
        // TODO переделать. Не находит записи по тэгам
        List<Post> posts = postRepository.findAllFilteredPosts();
        List<Post> filteredPosts = posts.stream()
                .filter(post -> tagService.tagExistInList(tag, post.getTags()))
                .collect(Collectors.toList());
        PostCountResponse postsCount = countPosts(filteredPosts, offset, limit);
        return new ResponseEntity<>(postsCount, HttpStatus.OK);
    }

    // Выбор сортировки отображаемых постов
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

    // Преобразование к количеству и списку постов для отображения
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
        String postTime = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm").format(post.getTime().minusHours(3));
        // Анонс поста (кусок текста) с проверкой кол-ва символов без HTML тэгов
        String postAnnounce = (post.getText().length() > MIN_ANNOUNCE_TEXT_LENGTH)
                ? post.getText().substring(0, MIN_ANNOUNCE_TEXT_LENGTH).concat("...")
                : post.getText();
        // Автор поста
        User author = post.getUser();
        // инициализация юзера
        UserIdNameResponse user = new UserIdNameResponse();
        user.setId(author.getId());
        user.setName(author.getName());
        // инициализация поста
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        postResponse.setTime(postTime);
        postResponse.setUser(user);
        postResponse.setTitle(post.getTitle());
        postResponse.setAnnounce(postAnnounce.replaceAll("(<.*?>)|(&.*?;)|([ ]{2,})", " "));
        postResponse.setLikeCount(0);      // TODO реализовать
        postResponse.setDislikeCount(0);   // TODO реализовать
        postResponse.setCommentCount(0);   // TODO реализовать
        postResponse.setViewCount(post.getViewCount());
        return postResponse;
    }
}