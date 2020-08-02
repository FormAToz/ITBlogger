package main.repository;

import main.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    String POST_DISPLAY_FILTER = "where p.active = 1 and p.moderationStatus = 'ACCEPTED' and p.time <= now()";

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
    List<Post> findPostsByQuery(@Param("query") String query);

    @Query(value = "from Post p " + POST_DISPLAY_FILTER + " and p.id = ?1")
    Post getPostById(@Param("id") int id);

    @Query("from Post p join Tag2Post t2p on t2p.postId = p.id join Tag t on t2p.tagId = t.id " + POST_DISPLAY_FILTER + " and t.name like ?1")
    List<Post> findFilteredPostsByTag(@Param("tagName") String tagName);
}
