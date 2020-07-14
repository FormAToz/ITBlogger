package main.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    private int id;

    @OneToOne
    @JoinColumn(name="parent_id", updatable = false)
    private PostComment parentId;

    @OneToOne(optional = false)
    @JoinColumn(name="post_id", updatable = false)
    private Post postId;

    @OneToOne(optional = false)
    @JoinColumn(name="user_id", updatable = false)
    private User userId;

    @NotNull
    private LocalDateTime time;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String text;

    public int getId() {
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
}
