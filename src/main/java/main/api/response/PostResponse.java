package main.api.response;

/**
 * Формат ответа:
 *
 * {
 * 		"id": 345,
 * 		"time": "Вчера, 17:32",
 * 		"user":
 *          {
 * 			"id": 88,
 * 			"name": "Дмитрий Петров"
 *           },
 * 		"title": "Заголовок поста",
 * 		"announce": "Текст анонса поста без HTML-тэгов",
 * 		"likeCount": 36,
 * 		"dislikeCount": 3,
 * 		"commentCount": 15,
 * 		"viewCount": 55
 * }
 */
public class PostResponse {
    private int id;
    private String time;
    private UserIdNameResponse user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;

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