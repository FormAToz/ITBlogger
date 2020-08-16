package main.api.response;

import main.api.response.user.UserResponse;

/**
 * {
 *     "id": 776,
 *     "time": "Вчера, 17:32",
 *     "text": "Текст комментария в формате HTML",
 *     "user":
 *       {
 *         "id": 88,
 *         "name": "Дмитрий Петров",
 *         "photo": "/avatars/ab/cd/ef/52461.jpg"
 *       }
 * }
 */
public class CommentResponse {
    private int id;
    private String time;
    private String text;
    private UserResponse user;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
