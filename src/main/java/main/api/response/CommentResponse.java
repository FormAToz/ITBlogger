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
    private final int id;
    private final String time;
    private final String text;
    private final UserResponse user;

    public CommentResponse(int id, String time, String text, UserResponse user) {
        this.id = id;
        this.time = time;
        this.text = text;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public UserResponse getUser() {
        return user;
    }
}
