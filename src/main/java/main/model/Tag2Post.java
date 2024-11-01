package main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tag2post")
public class Tag2Post {
    /**
     * Класс связей тегов с постами
     *
     * id      id связи
     * postId  id поста
     * tagId   id тэга
     * */

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="post_id", updatable = false, nullable = false)
    private long postId;

    @Column(name="tag_id", updatable = false, nullable = false)
    private long tagId;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
