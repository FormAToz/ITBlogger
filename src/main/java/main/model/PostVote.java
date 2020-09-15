package main.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_votes")
public class PostVote {
    /**
     * Класс лайков и дизлайков постов
     * ===============================
     * id       id лайка/дизлайка
     * user     тот, кто поставил лайк / дизлайк
     * post     пост, которому поставлен лайк / дизлайк
     * timestamp     дата и время лайка / дизлайка
     * value    лайк или дизлайк: 1 или -1
     * */

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="user_id", updatable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name="post_id", updatable = false)
    private Post post;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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
