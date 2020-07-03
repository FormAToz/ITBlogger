package main.model.repository;

import main.model.entity.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

    @Query(value = "from Post post where post.active = 1 and post.moderationStatus = 'ACCEPTED' and post.time <= now()")
    List<Post> findAllFilteredPosts();
}
