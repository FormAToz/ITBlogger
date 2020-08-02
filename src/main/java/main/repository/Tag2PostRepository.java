package main.repository;

import main.model.Tag2Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Tag2PostRepository extends CrudRepository<Tag2Post, Integer> {
    String TAG_COUNTS = "select t.name, count(t2p.tagId) as tagCount from Tag2Post t2p " +
            "join Tag t on t2p.tagId = t.id " +
            "join Post p on t2p.postId = p.id " +
            "where p.moderationStatus = 'ACCEPTED' and p.active = 1 and p.time <= now()" +
            "group by t2p.tagId order by tagCount desc";

    @Query(value = TAG_COUNTS)
    List<Object[]> allTagsCount();
}
