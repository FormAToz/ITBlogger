package main.api.request;

import main.model.Tag;

import java.util.List;

/**
 * Класс запроса при добавлении поста
 *
 * {
 *   "timestamp":1592338706,
 *   "active":1,
 *   "title":"заголовок",
 *   "tags":["java","spring"],
 *   "text":"Текст поста включащий <b>тэги форматирования</b>"
 * }
 */
public class PostRequest {
    private long timestamp;
    private int active;
    private String title;
    private List<Tag> tags;
    private String text;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
