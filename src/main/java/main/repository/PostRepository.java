package main.repository;

import main.model.Post;
import main.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    String POST_DISPLAY_FILTER = "where p.active = 1 and p.moderationStatus = 'ACCEPTED' and p.time <= now()";
    String ACTIVE_POSTS_FILTER = "from Post p where p.active = 1";

     /**
     * Поиск отфильтрованных постов
     * Вывод только активных (поле is_active в таблице Posts равно 1),
     * утверждённых модератором (поле moderation_status равно ACCEPTED)
     * постов с датой публикации не позднее текущего момента (движок должен позволять откладывать публикацию).
     */
    @Query(value = "from Post p " + POST_DISPLAY_FILTER)
    List<Post> findAllFilteredPosts();

    /**
     * Поиск в заголовке и тексте в отфильтрованных постах
     */
    @Query(value = "from Post p " + POST_DISPLAY_FILTER + " and p.title like %?1% or p.text like %?1%")
    List<Post> findFilteredPostsByQuery(@Param("query") String query);

    @Query(value = "from Post p " + POST_DISPLAY_FILTER + " and p.id = ?1")
    Optional<Post> findFilteredPostById(@Param("id") int id);

    @Query("from Post p join Tag2Post t2p on t2p.postId = p.id join Tag t on t2p.tagId = t.id " + POST_DISPLAY_FILTER + " and t.name like ?1")
    List<Post> findFilteredPostsByTag(@Param("tagName") String tagName);

    /**
     * Поиск всех не скрытых постов на модерацию
     */
    @Query(ACTIVE_POSTS_FILTER + " and p.moderationStatus != 'ACCEPTED'")
    List<Post> findAllPostsForModeration();

    /**
     * Поиск всех нескрытых постов по статусу модерации
     */
    @Query(ACTIVE_POSTS_FILTER + " and lower(p.moderationStatus) = lower(?1) order by p.time")
    List<Post> findAllPostsForModerationByStatus(@Param("status") String status);

    /**
     * Метод поиска постов по id и active
     */
    List<Post> findByUserAndActive(User user, int active);

    /**
     * Метод поиска постов по id, active и moderation_status
     */
    List<Post> findByUserAndActiveAndModerationStatus(User user, int active, Post.ModerationStatus moderationStatus);
}
