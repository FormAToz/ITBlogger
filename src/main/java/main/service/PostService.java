package main.service;

import main.Main;
import main.api.request.CommentRequest;
import main.api.request.PostRequest;
import main.api.response.CalendarResponse;
import main.api.response.IdResponse;
import main.api.response.StatisticsResponse;
import main.api.response.post.PostCountResponse;
import main.api.response.post.PostFullResponse;
import main.api.response.post.PostResponse;
import main.api.response.result.ResultResponse;
import main.api.response.user.UserResponse;
import main.exception.*;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.repository.PostRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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
    @Autowired
    private VoteService voteService;
    @Autowired
    private SettingsService settingsService;
    @Value("${post.active-value}")
    private int activeValue;
    @Value("${post.unactive-value}")
    private int unActiveValue;

    /**
     * Метод получает любой пост из репозитория по id
     *
     * @param id - id поста
     * @return Post
     * @throws PostNotFoundException в случае, если пост не найден
     */
    public Post getById(int id) throws PostNotFoundException {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Запрашиваемый пост с id = " + id + " не найден"));
    }

    /**
     * Метод получает не скрытый пост, со статусом ACCEPTED из репозитория по id
     *
     * @param id - id поста
     * @return  Post
     * @throws PostNotFoundException в случае, если пост не найден
     */
    public Post getActiveAndAcceptedPostById(int id) throws PostNotFoundException {
        return postRepository.findAvailablePostById(id)
                .orElseThrow(() -> new PostNotFoundException("Запрашиваемый пост с id = " + id + " не найден"));
    }

    /**
     * Список всех постов. Должны выводиться только активные (поле is_active в таблице posts равно 1),
     * утверждённые модератором (поле moderation_status равно ACCEPTED) посты с датой публикации не позднее текущего момента.
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести (10)
     * @param mode   - режим вывода (сортировка):
     *               recent - сортировать по дате публикации, выводить сначала новые
     *               popular - сортировать по убыванию количества комментариев
     *               best - сортировать по убыванию количества лайков
     *               early - сортировать по дате публикации, выводить сначала старые
     */
    public PostCountResponse getAllSortedPosts(int offset, int limit, String mode) {
        List<Post> posts = new ArrayList<>();
        Pageable pageable;
        long count = postRepository.countAllAvailablePosts().orElse(0L);

        if (mode.equals("recent")) {    // Сортировать по дате публикации, выводить сначала новые
            pageable = PageRequest.of(offset / limit, limit, Sort.by("time").descending());
            posts = postRepository.findAllAvailablePosts(pageable);

        }else if (mode.equals("popular")) {   // Сортировать по убыванию количества комментариев
            pageable = PageRequest.of(offset / limit, limit);
            posts = postRepository.findAvailablePostsByCommentCount(pageable);

        }else if (mode.equals("best")) {  // Сортировать по убыванию количества лайков
            pageable = PageRequest.of(offset / limit, limit);
            posts = postRepository.findAvailablePostsByVoteValue(pageable);

        }else if (mode.equals("early")) {     // Сортировать по дате публикации, выводить сначала старые
            pageable = PageRequest.of(offset / limit, limit, Sort.by("time").ascending());
            posts = postRepository.findAllAvailablePosts(pageable);
        }
        return migrateToPostCountResponse(count, posts);
    }

    /**
     * Метод добавления нового поста
     *
     * Метод отправляет данные поста, которые пользователь ввёл в форму публикации. В случае, если заголовок
     * или текст поста не установлены и/или слишком короткие, метод должен выводить ошибку и не добавлять пост.
     *
     * Пост должен сохраняться со статусом модерации NEW.
     */
    public ResultResponse addPost(PostRequest request) throws UserNotFoundException, InvalidParameterException {
        User user = userService.getUserFromSession();
        Post post = new Post();

        textService.checkTitleAndTextLength(request.getTitle(), request.getText());
        if (settingsService.preModerationIsOn()) {
            post.setModerationStatus(Post.ModerationStatus.NEW);
        }else {
            post.setModerationStatus(Post.ModerationStatus.ACCEPTED);
        }
        post.setActive(request.getActive());
        post.setUser(user);
        post.setTags(tagService.checkDuplicatesInRepo(request.getTags()));
        post.setTime(timeService.getExpectedTime(request.getTimestamp()));
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        post.setViewCount(0);
        postRepository.save(post);

        LOGGER.info(MARKER, "Пост добавлен. Id: {}, title: {}. User Id: {}, name: {}",
                post.getId(), post.getTitle(), post.getUser().getId(), post.getUser().getName());

        return new ResultResponse(true);
    }

    /**
     * Метод поиска постов по запросу
     *
     * Метод возвращает посты, соответствующие поисковому запросу - строке query.
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести
     * @param query  - поисковый запрос
     */
    public PostCountResponse searchPosts(int offset, int limit, String query) {
        long count = postRepository.countAllAvailablePostsByQuery(query).orElse(0L);
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> foundedPosts = postRepository.findAvailablePostsByQuery(query, pageable);
        return migrateToPostCountResponse(count, foundedPosts);
    }

    /**
     * Метод получения поста по id
     *
     * Метод выводит данные конкретного поста для отображения на странице поста, в том числе,
     * список комментариев и тэгов, привязанных к данному посту.
     *
     * @param id - id поста
     */
    public PostFullResponse getPostById(int id) throws UserNotFoundException, PostNotFoundException {
        User user = userService.getUserFromSession();
        Post post = getById(id);

        return (PostFullResponse) migrateToPostResponse(new PostFullResponse(), post)
                .active(isActive(post))
                .text(post.getText())
                .comments(commentService.migrateToCommentResponse(post))
                .tags(tagService.migrateToListTagName(post))
                .viewCount(incrementViews(user, post));
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
     * @param postRequest:
     *          timestamp - дата и время публикации в формате UTC
     *          active - 1 или 0, открыть пост или скрыть
     *          title - заголовок поста
     *          text - текст поста в формате HTML
     *          tags - тэги через запятую (при вводе на frontend тэг должен добавляться при нажатии Enter или вводе запятой).
     */
    public ResultResponse updatePost(int id, PostRequest postRequest)
            throws PostNotFoundException, InvalidParameterException, UserNotFoundException {
        Post post = getById(id);
        User user = userService.getUserFromSession();

        textService.checkTitleAndTextLength(postRequest.getTitle(), postRequest.getText());

        if (userService.isModerator(user)) {
            post.setModeratorId(user.getId());      // если модератор, меняем id модератора
        } else {
            post.setModerationStatus(Post.ModerationStatus.NEW);        // иначе меняем статус
        }

        post.setTime(timeService.getExpectedTime(postRequest.getTimestamp()));
        post.setActive(postRequest.getActive());
        post.setTitle(postRequest.getTitle());
        post.setText(postRequest.getText());
        post.setTags(tagService.checkDuplicatesInRepo(postRequest.getTags()));
        postRepository.save(post);

        return new ResultResponse(true);
    }

    /**
     * Метод проверки отображения поста.
     *
     * true если пост опубликован и false если скрыт (при этом модераторы и автор поста будет его видеть)
     */
    private boolean isActive(Post post) {
        return post.getActive() == activeValue;
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
     * Метод получения списка постов по тэгу
     * Метод выводит список постов, привязанных к тэгу, который был передан методу в качестве параметра tag.
     * @param offset  - сдвиг от 0 для постраничного вывода
     * @param limit   - количество постов, которое надо вывести (10)
     * @param tagName - тэг, по которому нужно вывести все посты
     */
    public PostCountResponse getPostsByTag(int offset, int limit, String tagName) {
        long count = postRepository.countAvailablePostsByTag(tagName).orElse(0L);
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> posts = postRepository.findAvailablePostsByTag(tagName, pageable);
        return migrateToPostCountResponse(count, posts);
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
        long count = postRepository.countAllActivePostsByStatus(status).orElse(0L);
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> posts = postRepository.findAllActivePostsByStatus(status, pageable);
        return migrateToPostCountResponse(count, posts);
    }

    /**
     * Метод подсчета всех постов, ожидающих модерации
     */
    public long countPostsForModeration() {
        return postRepository.countAllActiveAndUnmoderatedPosts().orElse(0L);
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

        Post post = getById(postId);
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
     * Метод получения списка постов авторизированного юзера (в соответствии с полем user_id в таблице posts базы данных).
     *
     * Возможны 4 типа вывода (см. ниже описания значений параметра status).
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit - количество постов, которое надо вывести
     * @param status - статус модерации:
     *              inactive - скрытые, ещё не опубликованы (is_active = 0);
     *              pending - активные, ожидают утверждения модератором (is_active = 1, moderation_status = NEW);
     *              declined - отклонённые по итогам модерации (is_active = 1, moderation_status = DECLINED);
     *              published - опубликованные по итогам модерации (is_active = 1, moderation_status = ACCEPTED).
     */
    public PostCountResponse getMyPosts(int offset, int limit, String status) throws UserNotFoundException {
        User user = userService.getUserFromSession();
        Pageable pageable = PageRequest.of(offset / limit, limit);;
        List<Post> list = new ArrayList<>();
        long count = 0L;

        switch (status) {
            case "inactive":
                count = postRepository.countByUserAndActive(user, unActiveValue).orElse(0L);
                list = postRepository.findByUserAndActive(user, unActiveValue, pageable);
                break;

            case "pending":
                count = postRepository.countByUserAndActiveAndModerationStatus(user, activeValue, Post.ModerationStatus.NEW).orElse(0L);
                list = postRepository.findByUserAndActiveAndModerationStatus(user, activeValue, Post.ModerationStatus.NEW, pageable);
                break;

            case "declined":
                count = postRepository.countByUserAndActiveAndModerationStatus(user, activeValue, Post.ModerationStatus.DECLINED).orElse(0L);
                list = postRepository.findByUserAndActiveAndModerationStatus(user, activeValue, Post.ModerationStatus.DECLINED, pageable);
                break;

            case "published":
                count = postRepository.countByUserAndActiveAndModerationStatus(user, activeValue, Post.ModerationStatus.ACCEPTED).orElse(0L);
                list = postRepository.findByUserAndActiveAndModerationStatus(user, activeValue, Post.ModerationStatus.ACCEPTED, pageable);
                break;
        }
        return migrateToPostCountResponse(count, list);
    }

    /**
     * Метод выводит количества публикаций на каждую дату переданного в параметре year года или текущего года,
     * если параметр year не задан. В параметре years всегда возвращается список всех годов,
     * за которые была хотя бы одна публикация, в порядке возврастания.
     *
     * Считаются только активные посты(поле is_active в таблице posts равно 1),
     * утверждённые модератором (поле moderation_status равно ACCEPTED),
     * с датой публикации не позднее текущего момента.
     *
     * @param year - год в виде четырёхзначного числа, если не передан - возвращать за текущий год
     */
    public CalendarResponse getAllPostsForCalendar(int year) {
        if (year == 0) {
            year = LocalDateTime.now().getYear();
        }

        List<Integer> years = postRepository.findAllAvailablePostsByYears();
        List<Object[]> allRecordsByYearFromRepo = postRepository.getAllAvailablePostsForYear(year);
        Map<String, Long> allRecordsByYear = new HashMap<>();

        allRecordsByYearFromRepo.forEach(el -> {
            String date = (String) el[0];   // date - "2019-12-17"
            long count = (long) el[1];      // count of date - 34
            allRecordsByYear.put(date, count);
        });

        return new CalendarResponse(years, allRecordsByYear);
    }

    /**
     * Метод получения списка постов за указанную дату
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести (10)
     * @param date   - дата в формате "2019-10-15"
     */
    public PostCountResponse getPostsByDate(int offset, int limit, String date) {
        long count = postRepository.countAllAvailablePostsByDate(date).orElse(0L);
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> posts = postRepository.findAllAvailablePostsByDate(date, pageable);

        return migrateToPostCountResponse(count, posts);
    }

    /**
     * Метод преобразования списка Post к списку PostCountResponse
     * @param count количество всех постов в базе данных, выбранных по определенному фильтру
     * @param list лимитированный список объектов Post, выбранных из базы данных
     * @return список объектов PostCountResponse
     */
    private PostCountResponse migrateToPostCountResponse(long count, List<Post> list) {
        List<PostResponse> postsList = new ArrayList<>();

        list.stream().forEach(post -> postsList.add(migrateToPostResponse(new PostResponse(), post)));
        return new PostCountResponse(count, postsList);
    }

    /**
     * Метод преобразования post -> postResponse
     */
    private <T extends PostResponse>T migrateToPostResponse(T postResponse, Post post) {
        User author = post.getUser();

        postResponse
                .id(post.getId())
                .timestamp(timeService.getTimestampFromLocalDateTime(post.getTime()))
                .user(new UserResponse(author.getId(), author.getName()))
                .title(post.getTitle())
                .announce(textService.getAnnounce(post.getText()))
                .likeCount(voteService.countLikesFromPost(post))
                .dislikeCount(voteService.countDislikesFromPost(post))
                .commentCount(post.getComments().size())
                .viewCount(post.getViewCount());

        return postResponse;
    }

    /**
     * Метод выдаёт статистику по всем постам блога.
     *
     * В случае, если публичный показ статистики блога запрещён (см. соответствующий параметр в global_settings)
     * и текущий пользователь не модератор, должна выдаваться ошибка 401.
     *
     * @return StatisticsResponse
     */
    public StatisticsResponse getGlobalStatistics()
            throws ApplicationException, UserNotFoundException {
        // если настройки показа отключены и пользователь не модератор
        if (!settingsService.globalStatisticsIsAvailable() && !userService.isModerator(userService.getUserFromSession())) {
            throw new ApplicationException("Статистика просмотра всего блога недоступна");
        }

        return new StatisticsResponse()
                .postsCount(postRepository.countAllActivePosts().orElse(0L))
                .likesCount(voteService.countAllLikes())
                .dislikesCount(voteService.countAllDislikes())
                .viewsCount(postRepository.countAllViewsFromAvailablePosts().orElse(0L))
                .firstPublication(timeService.getTimestampFromLocalDateTime(postRepository.getTimeOfFirstPost()));
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
    public IdResponse addComment(CommentRequest commentRequest)
            throws UserNotFoundException, PostNotFoundException, InvalidParameterException, CommentNotFoundException {
        int parentCommentId = commentRequest.getParentId();
        String text = commentRequest.getText();
        User user = userService.getUserFromSession();
        Post post = getActiveAndAcceptedPostById(commentRequest.getPostId());
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

    /**
     * Поиск постов, доступных для чтения, конкретного пользователя.
     *
     * @param user - пользователь, чьи посты нужно найти
     * @return - число постов, доступных для чтения
     */
    public long countPostsByUser(User user) {
        return postRepository.countAllAvailablePostsByUser(user).orElse(0L);
    }

    /**
     * Сумма всех просмотров постов, доступных для чтения, у конкретного пользователя.
     *
     * @param user - пользователь, чьи просмотры постов нужно получить
     * @return - сумма просмотров всех постов
     */
    public long countViewsFromPostsByUser(User user) {
        return postRepository.countViewsFromAllPostsByUser(user).orElse(0L);
    }

    /**
     * Получение времени первой публикации у конкретного пользователя.
     *
     * @param user - пользователь, чье время первой публикации надо получить
     * @return - время первой публикации или null, если не найдено ни одной публикации
     */
    public LocalDateTime getTimeOfFirstPostByUser(User user) {
        return postRepository.getTimeOfFirstPostByUser(user.getId());
    }
}