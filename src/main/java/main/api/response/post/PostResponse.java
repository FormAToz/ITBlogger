package main.api.response.post;

import main.api.response.user.UserResponse;

/**
 * Формат ответа:
 * {
 * 		"id": 345,
 * 		"timestamp": 1592338706,
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
    protected long id;
    protected long timestamp;
    protected UserResponse user;
    protected String title;
    protected String announce;
    protected long likeCount;
    protected long dislikeCount;
    protected long commentCount;
    protected long viewCount;

    public PostResponse() {
    }

    public PostResponse id(long id) {
        this.id = id;
        return this;
    }

    public PostResponse timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public PostResponse user(UserResponse user) {
        this.user = user;
        return this;
    }

    public PostResponse title(String title) {
        this.title = title;
        return this;
    }

    public PostResponse announce(String announce) {
        this.announce = announce;
        return this;
    }

    public PostResponse likeCount(long likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public PostResponse dislikeCount(long dislikeCount) {
        this.dislikeCount = dislikeCount;
        return this;
    }

    public PostResponse commentCount(long commentCount) {
        this.commentCount = commentCount;
        return this;
    }

    public PostResponse viewCount(long viewCount) {
        this.viewCount = viewCount;
        return this;
    }

     public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public UserResponse getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getAnnounce() {
        return announce;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public long getDislikeCount() {
        return dislikeCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public long getViewCount() {
        return viewCount;
    }
}
