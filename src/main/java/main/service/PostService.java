package main.service;

import main.Main;
import main.api.response.CalendarResponse;
import main.api.response.StatisticsResponse;
import main.api.response.post.PostCountResponse;
import main.api.response.post.PostFullResponse;
import main.api.response.post.PostResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.api.response.user.UserResponse;
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

import javax.annotation.PostConstruct;
import java.util.*;
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
     * Список постов
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
     * Добавление поста
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

        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    /**
     * Метод изменяет данные поста с идентификатором ID на те, которые пользователь ввёл в форму публикации.
     * В случае, если заголовок или текст поста не установлены и/или слишком короткие (короче 3 и 50 символов соответственно),
     * метод должен выводить ошибку и не изменять пост.
     *
     * Время публикации поста также должно проверяться: в случае, если время публикации раньше текущего времени,
     * оно должно автоматически становиться текущим. Если позже текущего - необходимо устанавливать указанное значение.
     *
     * Пост должен сохраняться со статусом модерации NEW, если его изменил автор, и статус модерации не должен изменяться,
     * если его изменил модератор.
     *
     * @param id - id поста
     * @param post:
     *          timestamp - дата и время публикации в формате UTC
     *          active - 1 или 0, открыть пост или скрыть
     *          title - заголовок поста
     *          text - текст поста в формате HTML
     *          tags - тэги через запятую (при вводе на frontend тэг должен добавляться при нажатии Enter или вводе запятой).
     */
    public ResponseEntity<ResultResponse> updatePost(int id, Post post) {
        if (true) {
            // TODO пост обновлен

            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }
        else {
            // TODO в случае ошибки
            Map<String, String> errors = new HashMap<>();

            return new ResponseEntity<>(new ErrorResultResponse(false, errors), HttpStatus.OK);
        }
    }

    /**
     * Поиск постов
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
     * Получение поста
     * Метод выводит данные конкретного поста для отображения на странице поста, в том числе,
     * список комментариев и тэгов, привязанных к данному посту.
     * @param id - id поста
     */
    public ResponseEntity<PostFullResponse> getPostById(int id) {
        PostFullResponse postFullResponse;
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

        postFullResponse = migrateToPostResponse(new PostFullResponse(), post);
        // TODO разобраться со значением
        // должно быть значение true если пост опубликован и false если скрыт (при этом модераторы и автор поста будет его видеть)
        postFullResponse.setActive(true);
        postFullResponse.setText(post.getText());
        // TODO проверить отображение
        postFullResponse.setComments(post.getComments());
        postFullResponse.setTags(post.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList()));
        // Пользователь не модератор и не автор
        // TODO реализовать сервис инкремента просмотров
        if (!userService.isModerator(user) && !userService.isAuthor(user, post)) {
            int viewCount = post.getViewCount();
            postFullResponse.setViewCount(++viewCount);
            post.setViewCount(viewCount);
            postRepository.save(post);
        }
        return new ResponseEntity<>(postFullResponse, HttpStatus.OK);
    }

    /**
     * Список постов за указанную дату
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
     * Список постов по тэгу
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
     * Метод выводит все посты, которые требуют модерационных действий (которые нужно утвердить или отклонить)
     * или над которыми мною были совершены модерационные действия: которые я отклонил или утвердил
     * (это определяется полями moderation_status и moderator_id в таблице posts базы данных).
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести
     * @param status - статус модерации:
     *               new - новые, необходима модерация;
     *               declined - отклонённые мной;
     *               accepted - утверждённые мной
     */
    public ResponseEntity<PostCountResponse> getPostsForModeration(int offset, int limit, String status) {
        // TODO
        PostCountResponse postCount = null;

        return new ResponseEntity<>(postCount, HttpStatus.OK);
    }

    /**
     * Метод фиксирует действие модератора по посту: его утверждение или отклонение.
     * Кроме того, фиксируется moderator_id - идентификатор пользователя, который отмодерировал пост.
     * Посты могут модерировать только пользователи с is_moderator = 1
     *
     * @param postId   - идентификатор поста
     * @param decision - решение по посту: accept или decline.
     */
    public ResponseEntity<ResultResponse> moderate(int postId, String decision) {
        if (true) {
            //TODO
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
    }

    /**
     * Метод выводит только те посты, которые создал я (в соответствии с полем user_id в таблице posts базы данных).
     * Возможны 4 типа вывода (см. ниже описания значений параметра status).
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit - количество постов, которое надо вывести
     * @param status - статус модерации:
     *              inactive - скрытые, ещё не опубликованы (is_active = 0);
     *              pending - активные, ожидают утверждения модератором (is_active = 1, moderation_status = NEW);
     *              declined - отклонённые по итогам модерации (is_active = 1, moderation_status = DECLINED);
     *              published - опубликованные по итогам модерации (is_active = 1, moderation_status = ACCEPTED).
     */
    public ResponseEntity<PostCountResponse> getMyPosts(int offset, int limit, String status) {
        // TODO
        PostCountResponse postCount = null;

        return new ResponseEntity<>(postCount, HttpStatus.OK);
    }

    /**
     * Метод выводит количества публикаций на каждую дату переданного в параметре year года или текущего года,
     * если параметр year не задан. В параметре years всегда возвращается список всех годов,
     * за которые была хотя бы одна публикация, в порядке возврастания.
     *
     * @param year - год в виде четырёхзначного числа, если не передан - возвращать за текущий год
     */
    public ResponseEntity<CalendarResponse> getAllPostsForCalendar(int year) {
        // TODO
        List<Integer> years = new ArrayList<>();
        Map<String, Integer> posts = new HashMap<>();
        return new ResponseEntity<>(new CalendarResponse(years, posts), HttpStatus.OK);
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
                    PostResponse postResponse = migrateToPostResponse(new PostResponse(), post);
                    postsList.add(postResponse);
                });
        return new PostCountResponse(count, postsList);
    }

    /**
     * Преобразование post -> postResponse
     */
    private <T extends PostResponse>T migrateToPostResponse(T postResponse, Post post) {
        User author = post.getUser();

        postResponse.setId(post.getId());
        postResponse.setTime(timeService.timeToString(post.getTime()));
        postResponse.setUser(new UserResponse(author.getId(), author.getName()));
        postResponse.setTitle(post.getTitle());
        postResponse.setAnnounce(textService.getAnnounce(post.getText()));
        postResponse.setLikeCount(0);      // TODO реализовать
        postResponse.setDislikeCount(0);   // TODO реализовать
        postResponse.setCommentCount(0);   // TODO реализовать
        postResponse.setViewCount(post.getViewCount());

        return postResponse;
    }

    /**
     * Метод выдаёт статистику по всем постам блога. В случае, если публичный показ статистики блога запрещён
     * (см. соответствующий параметр в global_settings) и текущий пользователь не модератор, должна выдаваться ошибка 401.
     */
    public ResponseEntity<StatisticsResponse> getGlobalStatistics() {
        // TODO реализовать
        return new ResponseEntity<>(new StatisticsResponse(), HttpStatus.OK);
    }

    /**
     * Метод сохраняет в таблицу post_votes лайк текущего авторизованного пользователя.
     * В случае повторного лайка возвращает {result: false}.
     *
     * Если до этого этот же пользователь поставил на этот же пост дизлайк, этот дизлайк должен быть заменен на лайк в базе данных.
     * @param postId - id поста которому ставим лайк
     */
    public ResponseEntity<ResultResponse> likePost(int postId) {
        if (true) {
            //TODO
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
    }

    /**
     * Метод сохраняет в таблицу post_votes дизлайк текущего авторизованного пользователя.
     * В случае повторного дизлайка возвращает {result: false}.
     *
     * Если до этого этот же пользователь поставил на этот же пост лайк, этот лайк должен заменен на дизлайк в базе данных.
     * @param postId - id поста
     */
    public ResponseEntity<ResultResponse> dislikePost(int postId) {
        if (true) {
            //TODO
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
    }
}