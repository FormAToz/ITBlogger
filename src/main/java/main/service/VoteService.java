package main.service;

import main.api.response.result.ResultResponse;
import main.exception.PostNotFoundException;
import main.exception.UserNotFoundException;
import main.model.Post;
import main.model.PostVote;
import main.model.User;
import main.repository.PostVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Класс обработки лайков и дизлайков
 */
@Service
public class VoteService {
    @Autowired
    private PostVoteRepository voteRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private TimeService timeService;

    /**
     * Метод сохраняет в таблицу post_votes лайк текущего авторизованного пользователя.
     * В случае повторного лайка возвращает {result: false}.
     *
     * Если до этого этот же пользователь поставил на этот же пост дизлайк, этот дизлайк должен быть заменен на лайк в базе данных.
     * @param postId - id поста которому ставим лайк
     */
    public ResultResponse likePost(int postId) throws PostNotFoundException, UserNotFoundException {
        Post post = postService.getPostById(postId);
        User user = userService.getUserFromSession();
        PostVote vote = new PostVote();

        // TODO проверить случай повторного лайка и смену лайка на дизлайк в случае повтора.
        vote.setUser(user);
        vote.setPost(post);
        vote.setTime(LocalDateTime.now());
        vote.setValue((byte) 1);
        voteRepository.save(vote);

        return new ResultResponse(true);
    }

    /**
     * Метод сохраняет в таблицу post_votes дизлайк текущего авторизованного пользователя.
     * В случае повторного дизлайка возвращает {result: false}.
     *
     * Если до этого этот же пользователь поставил на этот же пост лайк, этот лайк должен заменен на дизлайк в базе данных.
     * @param postId - id поста
     */
    public ResultResponse dislikePost(int postId) {
        if (true) {
            //TODO
            return new ResultResponse(true);
        }
        else {
            return new ResultResponse(false);
        }
    }
}
