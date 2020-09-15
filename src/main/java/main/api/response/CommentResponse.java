package main.api.response;

import main.api.response.user.UserResponse;

/**
 * {
 *     "id": 776,
 *     "timestamp": 1592338706,
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
    private final long timestamp;
    private final String text;
    private final UserResponse user;

    public CommentResponse(int id, long timestamp, String text, UserResponse user) {
        this.id = id;
        this.timestamp = timestamp;
        this.text = text;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public UserResponse getUser() {
        return user;
    }
}
