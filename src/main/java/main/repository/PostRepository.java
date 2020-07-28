package main.repository;

import main.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    String FILTERED_POST_QUERY = "from Post p where p.active = 1 and p.moderationStatus = 'ACCEPTED' and p.time <= now()";

     /*
     * Поиск отфильтрованных постов
     * Вывод только активных (поле is_active в таблице Posts равно 1),
     * утверждённых модератором (поле moderation_status равно ACCEPTED)
     * постов с датой публикации не позднее текущего момента (движок должен позволять откладывать публикацию).
     */
    @Query(value = FILTERED_POST_QUERY)
    List<Post> findAllFilteredPosts();

    // Поиск в заголовке и тексте в отфильтрованных постах
    @Query(value = FILTERED_POST_QUERY + " and p.title like %?1% or p.text like %?1%")
    List<Post> findPostsByQuery(@Param("query") String query);

    @Query(value = FILTERED_POST_QUERY + " and p.id = :id")
    Post getPostById(@Param("id")int id);
}
