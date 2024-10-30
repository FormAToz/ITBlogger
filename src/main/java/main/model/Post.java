package main.model;

import main.model.enums.Status;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {
    /**
     * Класс посты
     * ===========
     * id                 id поста
     * active             скрыта или активна публикация: 0 или 1
     * moderationStatus   статус модерации, по умолчанию значение "NEW".
     * moderatorId        id пользователя-модератора, принявшего решение, или NULL
     * user               автор поста
     * time               дата и время публикации поста
     * title              заголовок поста
     * text               текст поста
     * viewCount          количество просмотров поста
     */
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "is_active", columnDefinition = "TINYINT")
    private int active;

    @NotNull
    @Column(columnDefinition = "VARCHAR(32) default 'NEW'")
    @Enumerated(EnumType.STRING)
    private Status moderationStatus;

    @Column(name = "moderator_id")
    private Long moderatorId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @NotNull
    private LocalDateTime time;

    @NotNull
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String text;

    @NotNull
    @Column(name = "view_count")
    private int viewCount;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "tag2post",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @OneToMany(mappedBy = "postId", fetch = FetchType.LAZY)
    private List<PostComment> comments;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public Status getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(Status moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public Long getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(Long moderatorId) {
        this.moderatorId = moderatorId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<PostComment> getComments() {
        return comments;
    }

    public void setComments(List<PostComment> comments) {
        this.comments = comments;
    }
}
