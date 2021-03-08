package main.service;

import main.api.response.result.ResultResponse;
import main.exception.PostNotFoundException;
import main.exception.UserNotFoundException;
import main.model.Post;
import main.model.PostVote;
import main.model.User;
import main.repository.PostVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Класс обработки лайков и дизлайков
 */
@Service
public class VoteService {
    @Value("${vote.like-value}")
    private byte likeValue;
    @Value("${vote.dislike-value}")
    private byte dislikeValue;

    @Autowired
    private PostVoteRepository voteRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    /**
     * Метод сохраняет в таблицу post_votes лайк текущего авторизованного пользователя.
     * В случае повторного лайка возвращает {result: false}.
     *
     * Если до этого этот же пользователь поставил на этот же пост дизлайк, этот дизлайк должен быть заменен на лайк в базе данных.
     * @param postId - id поста которому ставим лайк
     */
    public ResultResponse likePost(int postId) throws PostNotFoundException, UserNotFoundException {
        return new ResultResponse(setVote(likeValue, postId));
    }

    /**
     * Метод сохраняет в таблицу post_votes дизлайк текущего авторизованного пользователя.
     * В случае повторного дизлайка возвращает {result: false}.
     *
     * Если до этого этот же пользователь поставил на этот же пост лайк, этот лайк должен заменен на дизлайк в базе данных.
     * @param postId - id поста
     */
    public ResultResponse dislikePost(int postId) throws PostNotFoundException, UserNotFoundException {
        return new ResultResponse(setVote(dislikeValue, postId));
    }

    /**
     * Метод создает новую запись, если лайка/дизлайка на пост не было.
     * Если лайк был, но пришел дизлайк, то меняем запись на дизлайк и наоборот.
     *
     * @param voteValue - значение лайка/дизлайка
     * @param postId - id поста, которому ставится лайк/дизлайк
     * @return  В случае повторного лайка/дизлайка - возвращаем false, иначе true
     * @throws PostNotFoundException в случае, если пост не найден
     * @throws UserNotFoundException в случае, если пользователь не найден
     */
    private boolean setVote(byte voteValue, int postId) throws PostNotFoundException, UserNotFoundException {
        Post post = postService.getActiveAndAcceptedPostById(postId);
        User user = userService.getLoggedUser();
        PostVote vote = voteRepository.findByUserAndPost(user, post).orElse(new PostVote());

        if (vote.getValue() == voteValue) {     // Если лайк/дизлайк уже был
            return false;
        }
        vote.setUser(user);
        vote.setPost(post);
        vote.setTime(LocalDateTime.now());
        vote.setValue(voteValue);    // Меняем на лайк/дизлайк
        voteRepository.save(vote);

        return true;
    }

    /**
     * Метод подсчета лайков у поста
     *
     * @param post - искомый пост
     * @return - кол-во лайков
     */
    public long countLikesFromPost(Post post) {
        return voteRepository.countVotesFromPost(likeValue, post).orElse(0L);
    }

    /**
     * Метод подсчета дизлайков у поста
     *
     * @param post - искомый пост
     * @return - кол-во дизлайков
     */
    public long countDislikesFromPost(Post post) {
        return voteRepository.countVotesFromPost(dislikeValue, post).orElse(0L);
    }

    /**
     * Метод подсчета всех лайков конкретного пользователя
     *
     * @param user - пользователь, чьи лайки нужно посчитать
     * @return - кол-во лайков
     */
    public long countLikesByUser(User user) {
        return voteRepository.countByValueAndUser(likeValue, user).orElse(0L);
    }

    /**
     * Метод подсчета всех дизлайков конкретного пользователя
     *
     * @param user - пользователь, чьи дизлайки нужно посчитать
     * @return - кол-во дизлайков
     */
    public long countDislikesByUser(User user) {
        return voteRepository.countByValueAndUser(dislikeValue, user).orElse(0L);
    }

    /**
     * Метод подсчета всех лайков.
     *
     * @return - кол-во всех лайков
     */
    public long countAllLikes() {
       return voteRepository.countByValue(likeValue).orElse(0L);
    }

    /**
     * Метод подсчета всех дизлайков.
     *
     * @return - кол-во всех дизлайков
     */
    public long countAllDislikes() {
        return voteRepository.countByValue(dislikeValue).orElse(0L);
    }
}
