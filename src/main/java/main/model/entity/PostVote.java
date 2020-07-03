package main.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_votes")
public class PostVote {
    /**
     *  Класс лайков и дизлайков постов
     *
     * id       id лайка/дизлайка
     * userId  тот, кто поставил лайк / дизлайк
     * postId  пост, которому поставлен лайк / дизлайк
     * time     дата и время лайка / дизлайка
     * value    лайк или дизлайк: 1 или -1
     * */

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(optional = false)
    @JoinColumn(name="user_id", updatable = false)
    private User userId;

    @OneToOne(optional = false)
    @JoinColumn(name="post_id", updatable = false)
    private Post postId;

    @NotNull
    private LocalDateTime time;

    @NotNull
    private byte value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Post getPostId() {
        return postId;
    }

    public void setPostId(Post postId) {
        this.postId = postId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }
}
