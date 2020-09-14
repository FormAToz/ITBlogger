package main.service;

import main.Main;
import main.api.request.CommentRequest;
import main.api.response.CalendarResponse;
import main.api.response.IdResponse;
import main.api.response.StatisticsResponse;
import main.api.response.post.PostCountResponse;
import main.api.response.post.PostFullResponse;
import main.api.response.post.PostResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.api.response.user.UserResponse;
import main.exception.PostNotFoundException;
import main.exception.InvalidParameterException;
import main.exception.UserNotFoundException;
import main.model.Post;
import main.model.PostComment;
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
    @Autowired
    private CommentService commentService;

    public Post getPostById(int id) throws PostNotFoundException {
        return postRepository.getFilteredPostById(id)
                .orElseThrow(() -> new PostNotFoundException("Пост не найден"));
    }

    /**
     * Список всех постов
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести (10)
     * @param mode   - режим вывода (сортировка):
     *               recent - сортировать по дате публикации, выводить сначала новые
     *               popular - сортировать по убыванию количества комментариев
     *               best - сортировать по убыванию количества лайков
     *               early - сортировать по дате публикации, выводить сначала старые
     */
    public PostCountResponse getPosts(int offset, int limit, String mode) {
        List<Post> filteredPosts = sortFilteredPosts(postRepository.findAllFilteredPosts(), mode);

        return countPosts(filteredPosts, offset, limit);
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
            postComparator = Comparator.comparing((Post post) -> post.getComments().size()).reversed();
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
     *
     * Метод отправляет данные поста, которые пользователь ввёл в форму публикации. В случае, если заголовок
     * или текст поста не установлены и/или слишком короткие, метод должен выводить ошибку и не добавлять пост.
     *
     * Пост должен сохраняться со статусом модерации NEW.
     */
    public ResultResponse addPost(Post post) throws UserNotFoundException, InvalidParameterException {
        User user = userService.getUserFromSession();

        textService.checkTitleAndTextLength(post.getTitle(), post.getText());

        post.setUser(user);
        post.setTags(tagService.checkDuplicatesInRepo(post.getTags()));
        post.setTime(timeService.getExpectedTime(post.getTime()));
        post.setModerationStatus(Post.ModerationStatus.NEW);
        post.setViewCount(0);
        postRepository.save(post);

        LOGGER.info(MARKER, "Пост добавлен. Id: {}, title: {}. User Id: {}, name: {}",
                post.getId(), post.getTitle(), post.getUser().getId(), post.getUser().getName());

        return new ResultResponse(true);
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
    public ResultResponse updatePost(int id, Post post) {
        if (true) {
            // TODO пост обновлен

            return new ResultResponse(true);
        }
        else {
            // TODO в случае ошибки
            Map<String, String> errors = new HashMap<>();

            return new ErrorResultResponse(false, errors);
        }
    }

    /**
     * Поиск постов
     * Метод возвращает посты, соответствующие поисковому запросу - строке query.
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести
     * @param query  - поисковый запрос
     */
    public PostCountResponse searchPosts(int offset, int limit, String query) {
        List<Post> foundedPosts = postRepository.findFilteredPostsByQuery(query);
        return countPosts(foundedPosts, offset, limit);
    }

    /**
     * Получение поста
     *
     * Метод выводит данные конкретного поста для отображения на странице поста, в том числе,
     * список комментариев и тэгов, привязанных к данному посту.
     *
     * @param id - id поста
     */
    public PostFullResponse getPostResponseById(int id) throws UserNotFoundException, PostNotFoundException {
        User user = userService.getUserFromSession();
        Post post =
                (userService.isModerator(user)   // пост не будет получен, если пользователь не модератор
                        ? postRepository.findById(id)
                        : postRepository.getFilteredPostById(id))
                        .orElseThrow(() -> new PostNotFoundException("Запрашиваемый пост с id = " + id + " не найден"));

        // TODO проверить изменение счетчика просмотров
        return (PostFullResponse) migrateToPostResponse(new PostFullResponse(), post)
                .active(isActive(post))
                .text(post.getText())
                .comments(commentService.migrateToCommentResponse(post))
                .tags(tagService.migrateToListTagName(post))
                .viewCount(incrementViews(user, post));
    }


    /**
     * true если пост опубликован и false если скрыт (при этом модераторы и автор поста будет его видеть)
     */
    private boolean isActive(Post post) {
        return post.getActive() == 1;
    }


    /**
     * Метод увеличения кол-ва просмотров
     */
    private int incrementViews(User user, Post post) {
        int viewCount = post.getViewCount();

        // если пользователь не модератор и не автор
        if (!userService.isModerator(user) && !userService.isAuthor(user, post)) {
            post.setViewCount(++viewCount);
            postRepository.save(post);
        }
        return viewCount;
    }


    /**
     * Список постов за указанную дату
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести (10)
     * @param date   - дата в формате "2019-10-15"
     */
    public PostCountResponse getPostsByDate(int offset, int limit, String date) {
        // TODO проверить дату и реализовать вывод по указанной дате
        return countPosts(postRepository.findAllFilteredPosts(), offset, limit);
    }


    /**
     * Список постов по тэгу
     * Метод выводит список постов, привязанных к тэгу, который был передан методу в качестве параметра tag.
     * @param offset  - сдвиг от 0 для постраничного вывода
     * @param limit   - количество постов, которое надо вывести (10)
     * @param tagName - тэг, по которому нужно вывести все посты
     */
    public PostCountResponse getPostsByTag(int offset, int limit, String tagName) {
        return countPosts(postRepository.findFilteredPostsByTag(tagName), offset, limit);
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
    public PostCountResponse getPostsForModeration(int offset, int limit, String status) {
        return countPosts(postRepository.findAllPostsForModerationByStatus(status), offset, limit);
    }


    /**
     * Метод подсчета всех постов, ожидающих модерации
     */
    public int countPostsForModeration() {
        return postRepository.findAllPostsForModeration().size();
    }


    /**
     * Метод фиксирует действие модератора по посту: его утверждение или отклонение.
     * Кроме того, фиксируется moderator_id - идентификатор пользователя, который отмодерировал пост.
     * Посты могут модерировать только пользователи с is_moderator = 1
     *
     * @param postId   - идентификатор поста
     * @param decision - решение по посту: accept или decline.
     */
    public boolean moderate(int postId, String decision) throws UserNotFoundException, PostNotFoundException {
        User user = userService.getUserFromSession();

        if (!userService.isModerator(user)) {
            return false;
        }

        Post post = postRepository.findById(postId).orElseThrow(() -> {
                            String message = "Запрашиваемый пост с id = " + postId + " не найден";
                            LOGGER.info(MARKER, message);
                            return new PostNotFoundException(message);
                        });

        post.setModeratorId(user.getId());
        post.setModerationStatus(getModerationStatus(post.getModerationStatus(), decision));
        postRepository.save(post);

        LOGGER.info(MARKER,
                "Статус поста с id = {} успешно обновлен на {}", post.getId(), post.getModerationStatus().name());

        return true;
    }


    /**
     * Метод устанавливает новый статус посту, если статус найден. В противном случае возвращает старый статус.
     */
    private Post.ModerationStatus getModerationStatus(Post.ModerationStatus oldStatus, String strStatus) {
        switch (strStatus) {
            case "accept":
                return Post.ModerationStatus.ACCEPTED;

            case "decline":
                return Post.ModerationStatus.DECLINED;

            default:
                return oldStatus;
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
    public PostCountResponse getMyPosts(int offset, int limit, String status) {
        // TODO
        PostCountResponse postCount = null;

        return postCount;
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
                    postsList.add(migrateToPostResponse(new PostResponse(), post));
                });
        return new PostCountResponse(count, postsList);
    }

    /**
     * Преобразование post -> postResponse
     */
    private <T extends PostResponse>T migrateToPostResponse(T postResponse, Post post) {
        User author = post.getUser();

        postResponse
                .id(post.getId())
                .time(timeService.timeToString(post.getTime()))
                .user(new UserResponse(author.getId(), author.getName()))
                .title(post.getTitle())
                .announce(textService.getAnnounce(post.getText()))
                .likeCount(0)      // TODO реализовать
                .dislikeCount(0)   // TODO реализовать
                .commentCount(post.getComments().size())
                .viewCount(post.getViewCount());

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
     * Метод добавляет комментарий к посту. Должны проверяться все три параметра.
     * Если параметры parent_id и/или post_id неверные (соответствующие комментарий и/или пост не существуют),
     * должна выдаваться ошибка 400 (см. раздел “Обработка ошибок”).
     * В случае, если текст комментария отсутствует (пустой) или слишком короткий, необходимо выдавать ошибку в JSON-формате.
     *
     * Пример запроса на добавление комментария к самому посту:
     *
     * {
     *   "parent_id":"",
     *   "post_id":21,
     *   "text":"привет, какой интересный пост!"
     * }
     * Пример запроса на добавление комментария к другому комментарию:
     *
     * {
     *   "parent_id":31,
     *   "post_id":21,
     *   "text":"текст комментария"
     * }
     *
     * parent_id - ID комментария, на который пишется ответ
     * post_id - ID поста, к которому пишется ответ
     * text - текст комментария (формат HTML)
     */
    public IdResponse addComment(CommentRequest commentRequest) throws UserNotFoundException, PostNotFoundException, InvalidParameterException {
        int parentCommentId = commentRequest.getParentId();
        String text = commentRequest.getText();
        User user = userService.getUserFromSession();
        Post post = postRepository.getFilteredPostById(commentRequest.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Запрашиваемый пост с id = " + parentCommentId + " не найден"));
        PostComment parentComment;
        PostComment comment = new PostComment();

        textService.checkTextCommentLength(text);

        // Если Id родительского комментария установлен, то ставим комментарий к родительскому комментарию
        if (parentCommentId != 0) {

            parentComment = commentService.getCommentById(parentCommentId);
            comment.setParentId(parentComment);
        }

        comment.setPostId(post);
        comment.setText(text);
        comment.setTime(LocalDateTime.now());
        comment.setUserId(user);
        commentService.saveComment(comment);

        return new IdResponse(comment.getId());
    }
}