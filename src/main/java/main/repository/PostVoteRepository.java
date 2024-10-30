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
public interface PostVoteRepository extends CrudRepository<PostVote, Long> {

    /**
     * Метод получения лайка/дизлайка по автору лайка/дизлайка и посту
     *
     * @param user - автор лайка/дизлайка
     * @param post - пост, который оценивали
     * @return Optional<PostVote>
     */
    Optional<PostVote> findByUserAndPost(User user, Post post);

    /**
     * Метод подсчета общего кол-ва лайков/дизлайков
     *
     * @param value - значение лайка/дизлайка
     * @return - кол-во лайков\дизлайков
     */
    Optional<Long> countByValue(int value);

    /**
     * Метод подсчета лайков/дизлайков у поста
     *
     * @param voteValue - значение (лайк или дизлайк)
     * @param post - искомый пост
     * @return - кол-во лайков/дизлайков
     */
    @Query(value = "select count(*) from PostVote pv where pv.value = ?1 and pv.post = ?2")
    Optional<Long> countVotesFromPost(@Param("voteValue") int voteValue, @Param("post") Post post);

    /**
     * Метод подсчета всех лайков/дизлайков конкретного пользователя
     *
     * @param value - значение лайка/дизлайка
     * @param user - пользователь, чьи голоса нужно посчитать
     * @return - кол-во голосов
     */
    Optional<Long> countByValueAndUser(int value, User user);
}

