package main.repository;

import main.model.Post;
import main.model.User;
import main.model.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    String AVAILABLE_POSTS_FILTER = "where p.active = 1 and p.moderationStatus = 'ACCEPTED' and p.time <= now()";
    String ACTIVE_POSTS_FILTER = "from Post p where p.active = 1";

    /**
     * Метод подсчета всех не скрытых постов (в том числе не проверенных модератором)
     *
     * @return - кол-во постов
     */
    @Query(value = "select count(*)" + ACTIVE_POSTS_FILTER)
    Optional<Long> countAllActivePosts();

    /**
     * Количество всех не скрытых и не принятых на модерацию постов
     */
    @Query("select count(*)" + ACTIVE_POSTS_FILTER + " and p.moderationStatus != 'ACCEPTED'")
    Optional<Long> countAllActiveAndUnmoderatedPosts();

    /**
     * Количество всех доступных для отображения постов
     */
    @Query(value = "select count(*) from Post p " + AVAILABLE_POSTS_FILTER)
    Optional<Long> countAllAvailablePosts();

    /**
     * Количество постов, доступных для чтения, конкретного пользователя.
     *
     * @param user - пользователь, чьи посты нужно найти
     * @return - число постов, доступных для чтения
     */
    @Query(value = "select count(*) from Post p " + AVAILABLE_POSTS_FILTER + " and p.user = ?1")
    Optional<Long> countAllAvailablePostsByUser(@Param("user") User user);

    /**
     * Количество просмотров всех постов, доступных для чтения
     *
     * @return - кол-во просмотров
     */
    @Query(value = "select sum(p.viewCount) from Post p " + AVAILABLE_POSTS_FILTER)
    Optional<Long> countAllViewsFromAvailablePosts();

    /**
     * Сумма всех просмотров постов, доступных для чтения, у конкретного пользователя.
     *
     * @param user - пользователь, чьи просмотры постов нужно получить
     * @return - сумма просмотров всех постов
     */
    @Query(value = "select sum(p.viewCount) from Post p " + AVAILABLE_POSTS_FILTER + " and p.user = ?1")
    Optional<Long> countViewsFromAllPostsByUser(User user);

    /**
     * Поиск доступных постов по убыванию количества комментариев
     */
    @Query(value = "from Post p " + AVAILABLE_POSTS_FILTER + " order by size(p.comments) desc")
    List<Post> findAvailablePostsByCommentCount(Pageable pageable);

    /**
     * Поиск постов по значению лайка/дизлайка
     */
    @Query(value = "from Post p left join PostVote pv on p.id = pv.post.id " + AVAILABLE_POSTS_FILTER + " and pv.value = 1 or pv.value is null group by p.id order by sum(pv.value) desc")
    List<Post> findAvailablePostsByVoteValue(Pageable pageable);

    /**
     * Поиск отфильтрованных постов
     * Вывод только активных (поле is_active в таблице Posts равно 1),
     * утверждённых модератором (поле moderation_status равно ACCEPTED)
     * постов с датой публикации не позднее текущего момента (движок должен позволять откладывать публикацию).
     */
    @Query(value = "from Post p " + AVAILABLE_POSTS_FILTER)
    List<Post> findAllAvailablePosts(Pageable pageable);

    /**
     * Поиск в заголовке и тексте в отфильтрованных постах
     */
    @Query(value = "from Post p " + AVAILABLE_POSTS_FILTER + " and p.title like %?1% or p.text like %?1%")
    List<Post> findAvailablePostsByQuery(@Param("query") String query, Pageable pageable);

    /**
     * Количество постов, в заголовках и теле которых присутствует строка запроса
     */
    @Query(value = "select count(*) from Post p " + AVAILABLE_POSTS_FILTER + " and p.title like %?1% or p.text like %?1%")
    Optional<Long> countAllAvailablePostsByQuery(@Param("query") String query);

    @Query(value = "from Post p " + AVAILABLE_POSTS_FILTER + " and p.id = ?1")
    Optional<Post> findAvailablePostById(@Param("id") int id);

    @Query("from Post p join Tag2Post t2p on t2p.postId = p.id join Tag t on t2p.tagId = t.id " + AVAILABLE_POSTS_FILTER + " and t.name like ?1")
    List<Post> findAvailablePostsByTag(@Param("tagName") String tagName, Pageable pageable);

    @Query("select count(*) from Post p join Tag2Post t2p on t2p.postId = p.id join Tag t on t2p.tagId = t.id " + AVAILABLE_POSTS_FILTER + " and t.name like ?1")
    Optional<Long> countAvailablePostsByTag(@Param("tagName") String tagName);

    /**
     * Поиск всех нескрытых постов по статусу модерации
     */
    @Query(ACTIVE_POSTS_FILTER + " and p.moderationStatus = ?1 order by p.time")
    List<Post> findAllActivePostsByStatus(@Param("status") Status status, Pageable pageable);

    /**
     * Количество всех активных постов по статусу модерации
     */
    @Query("select count(*)" + ACTIVE_POSTS_FILTER + " and p.moderationStatus = ?1")
    Optional<Long> countAllActivePostsByStatus(@Param("status") Status status);

    /**
     * Метод поиска постов по id и active
     */
    List<Post> findByUserAndActive(User user, int active, Pageable pageable);

    /**
     * Количество постов пользователя по id и active
     */
    Optional<Long> countByUserAndActive(User user, int active);

    /**
     * Метод поиска постов по id, active и moderation_status
     */
    List<Post> findByUserAndActiveAndModerationStatus(User user, int active, Status moderationStatus, Pageable pageable);

    /**
     * Количество постов по пользователю, активности поста и статусу модерации
     */
    Optional<Long> countByUserAndActiveAndModerationStatus(User user, int active, Status moderationStatus);

    /**
     * Метод группировки публикации постов по годам
     */
    @Query(value = "select year(p.time) as years from Post p " + AVAILABLE_POSTS_FILTER + " group by years order by years desc")
    List<Integer> findAllAvailablePostsByYears();

    /**
     * Метод поиска количества постов за год.
     * Отображение - дата/кол-во постов
     *
     * @param year - год, в котором посты были опубликованы
     */
    @Query(value = "select date_format(p.time, '%Y-%m-%d') as date_format, count(time) " +
            "from Post p " + AVAILABLE_POSTS_FILTER + " and year(p.time) = ?1 group by date_format order by date_format")
    List<Object[]> getAllAvailablePostsForYear(@Param("year") int year);

    /**
     * Метод поиска всех постов за указанную дату
     *
     * @param date - искомая дата
     */
    @Query(value = "from Post p " + AVAILABLE_POSTS_FILTER + " and date_format(p.time, '%Y-%m-%d') = ?1")
    List<Post> findAllAvailablePostsByDate(@Param("date") String date, Pageable pageable);

    /**
     * Количество постов за указанную дату
     */
    @Query(value = "select count(*) from Post p " + AVAILABLE_POSTS_FILTER + " and date_format(p.time, '%Y-%m-%d') = ?1")
    Optional<Long> countAllAvailablePostsByDate(@Param("date") String date);

    /**
     * Метод поиска времени первой публикации.
     *
     * @return - Время первой публикации
     */
    @Query(nativeQuery = true, value = "SELECT time FROM posts where is_active = 1 and moderation_status = 'ACCEPTED' order by time limit 1")
    LocalDateTime getTimeOfFirstPost();

    /**
     * Получение времени первой публикации, доступной для чтения, у конкретного пользователя.
     *
     * @param id - id пользователя, чье время первой публикации надо получить
     * @return - время первой публикации
     */
    @Query(nativeQuery = true, value = "SELECT time FROM posts where is_active = 1 and moderation_status = 'ACCEPTED' and user_id = :userId order by time limit 1")
    LocalDateTime getTimeOfFirstPostByUser(@Param("userId") int id);
}