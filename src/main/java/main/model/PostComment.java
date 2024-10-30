package main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "post_comments")
public class PostComment {
    /**
     * Класс комментариев к постам
     *
     * id          id комментария
     * parentId    комментарий, на который оставлен этот комментарий (может быть NULL, если комментарий оставлен просто к посту)
     * postId      пост, к которому написан комментарий
     * userId      автор комментария
     * time        дата и время комментария
     * text        текст комментария
     * */

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="parent_id")
    private PostComment parentId;

    @ManyToOne
    @JoinColumn(name="post_id", updatable = false, nullable = false)
    private Post postId;

    @ManyToOne
    @JoinColumn(name="user_id", updatable = false, nullable = false)
    private User userId;

    @NotNull
    private LocalDateTime time;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String text;

    @OneToMany(mappedBy = "parentId", fetch = FetchType.LAZY)
    private List<PostComment> comments;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PostComment getParentId() {
        return parentId;
    }

    public void setParentId(PostComment parentId) {
        this.parentId = parentId;
    }

    public Post getPostId() {
        return postId;
    }

    public void setPostId(Post postId) {
        this.postId = postId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<PostComment> getComments() {
        return comments;
    }

    public void setComments(List<PostComment> comments) {
        this.comments = comments;
    }
}
