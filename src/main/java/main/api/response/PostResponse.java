package main.api.response;

import main.model.PostComment;
import main.model.Tag;

import java.util.List;

/**
 * Формат ответа:
 *
 * {
 * 		"id": 345,
 * 		"time": "Вчера, 17:32",
 * 	    "active": true,
 * 		"user":
 *          {
 * 			"id": 88,
 * 			"name": "Дмитрий Петров"
 *           },
 * 		"title": "Заголовок поста",
 * 	    "text": "Текст поста в формате HTML",
 * 		"announce": "Текст анонса поста без HTML-тэгов",
 * 		"likeCount": 36,
 * 		"dislikeCount": 3,
 * 		"commentCount": 15,
 * 		"viewCount": 55,
 *      "comments": [
 *          {
 *              "id": 776,
 *              "time": "Вчера, 17:32",
 *              "text": "Текст комментария в формате HTML",
 *              "user":
 *          {
 *              "id": 88,
 *              "name": "Дмитрий Петров",
 *              "photo": "/avatars/ab/cd/ef/52461.jpg"
 *          }
 *          },
 *          {...}
 *  ],
 *  "tags": ["Статьи", "Java"]
 * }
 */
public class PostResponse {
    private int id;
    private String time;
    private boolean active;
    private UserIdNameResponse user;
    private String title;
    private String text;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private List<PostComment> comments;
    private List<String> tags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UserIdNameResponse getUser() {
        return user;
    }

    public void setUser(UserIdNameResponse user) {
        this.user = user;
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

    public String getAnnounce() {
        return announce;
    }

    public void setAnnounce(String announce) {
        this.announce = announce;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public List<PostComment> getComments() {
        return comments;
    }

    public void setComments(List<PostComment> comments) {
        this.comments = comments;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
