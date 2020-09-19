package main.repository;

import main.model.Post;
import main.model.PostVote;
import main.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVoteRepository extends CrudRepository<PostVote, Integer> {

    /**
     * Метод получения лайка/дизлайка по автору лайка/дизлайка и посту
     *
     * @param user - автор лайка/дизлайка
     * @param post - пост, который оценивали
     * @return Optional<PostVote>
     */
    Optional<PostVote> findByUserAndPost(User user, Post post);

    /**
     * Метод подсчета лайков/дизлайков у поста
     *
     * @param voteValue - значение (лайк или дизлайк)
     * @param post - искомый пост
     * @return - кол-во лайков/дизлайков
     */
    @Query(value = "select count(*) from PostVote pv where pv.value = ?1 and pv.post = ?2")
    int countVotesFromPost(@Param("voteValue") byte voteValue, @Param("post") Post post);
}

