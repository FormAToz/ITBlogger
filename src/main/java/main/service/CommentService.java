package main.service;

import main.Main;
import main.api.response.CommentResponse;
import main.api.response.user.UserResponse;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.repository.PostCommentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");

    @Autowired
    private PostCommentRepository commentRepository;
    @Autowired
    private TimeService timeService;

    public PostComment getCommentById(int id) {
        Optional comment = commentRepository.findById(id);

        if (!comment.isPresent()) {
            LOGGER.info(MARKER, "Комментарий с id={} не найден", id);
            return null;
        }
        return (PostComment) comment.get();
    }

    public void saveComment(PostComment comment) {
        commentRepository.save(comment);
    }

    /**
     * Преобразование post -> List<CommentResponse>
     */
    public List<CommentResponse> migrateToCommentResponse(Post post) {
        List<CommentResponse> commentList = new ArrayList<>();

        post.getComments().forEach(comment -> {
            User author = comment.getUserId();

            commentList.add(
                    new CommentResponse(
                            comment.getId(),
                            timeService.timeToString(comment.getTime()),
                            comment.getText(),
                            new UserResponse(author.getId(), author.getName(), author.getPhoto())
                    )
            );
        });

        return commentList;
    }
}
