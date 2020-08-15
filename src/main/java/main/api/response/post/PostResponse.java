package main.api.response.post;

import main.api.response.user.UserResponse;

/**
 * Формат ответа:
 * {
 * 		"id": 345,
 * 		"time": "02 августа 2020, 22:34",
 * 		"user":
 *               {
 * 			"id": 88,
 * 			"name": "Дмитрий Петров"
 *               },
 * 		"title": "Заголовок поста",
 * 		"announce": "Текст анонса поста без HTML-тэгов",
 * 		"likeCount": 36,
 * 		"dislikeCount": 3,
 * 		"commentCount": 15,
 * 		"viewCount": 55
 * 	}
 */
public class PostResponse {
    protected int id;
    protected String time;
    protected UserResponse user;
    protected String title;
    protected String announce;
    protected int likeCount;
    protected int dislikeCount;
    protected int commentCount;
    protected int viewCount;

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

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
