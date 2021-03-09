package main.service;

import main.api.response.CommentResponse;
import main.api.response.user.UserResponse;
import main.exception.ContentNotFoundException;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.repository.PostCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private PostCommentRepository commentRepository;
    @Autowired
    private TimeService timeService;

    /**
     * Метод получения комментария по id
     *
     * @param id - id комментария
     * @return PostComment
     * @throws ContentNotFoundException в случае, если комментарий не найден
     */
    public PostComment getCommentById(int id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Комментарий с id = " + id + " не найден"));
    }

    /**
     * Метод сохранения комментария в репозиторий
     *
     * @param comment - сохраняемы комментарий
     */
    public void saveComment(PostComment comment) {
        commentRepository.save(comment);
    }

    /**
     * Метод преобразования post -> List<CommentResponse>
     */
    public List<CommentResponse> migrateToCommentResponse(Post post) {
        List<CommentResponse> commentList = new ArrayList<>();

        post.getComments().forEach(comment -> {
            User author = comment.getUserId();

            commentList.add(
                    new CommentResponse(
                            comment.getId(),
                            timeService.getTimestampFromLocalDateTime(comment.getTime()),
                            comment.getText(),
                            new UserResponse(author.getId(), author.getName(), author.getPhoto())
                    )
            );
        });
        return commentList;
    }
}