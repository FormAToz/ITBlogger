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
     *
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
        List<Post> sortedFilteredPosts = sortFilteredPosts(postRepository.findAllFilteredPosts(), mode);
        PostCountResponse postsCount = countPosts(sortedFilteredPosts, offset, limit);
        return new ResponseEntity<>(postsCount, HttpStatus.OK);
    }

    // Добавление поста - POST /api/post
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
        LOGGER.info(MARKER, "Пост добавлен. Id: {}, user Id, name: {}, {}, title: {}",
                post.getId(), post.getUser().getId(),post.getUser().getName(), post.getTitle());
        return new ResponseEntity<>(new ResultResponse(true, null), HttpStatus.OK);
    }

    /**
     * Поиск постов - GET /api/post/search/
     * Метод возвращает посты, соответствующие поисковому запросу - строке query.
     *
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
                    String postTime = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm").format(post.getTime().minusHours(3));
                    // Анонс поста (кусок текста) с проверкой кол-ва символов без HTML тэгов
                    // TODO убрать HTML тэги
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
                    postResponse.setAnnounce(postAnnounce);
                    postResponse.setLikeCount(0);      // TODO реализовать
                    postResponse.setDislikeCount(0);   // TODO реализовать
                    postResponse.setCommentCount(0);   // TODO реализовать
                    postResponse.setViewCount(0);      // TODO реализовать
                    // добавление поста в список постов
                    postsList.add(postResponse);
                });
        return new PostCountResponse(count, postsList);
    }
}
