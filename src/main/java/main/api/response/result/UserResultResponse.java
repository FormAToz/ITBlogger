package main.api.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.api.response.user.UserFullResponse;

/**
 * Форматы ответов:
 *
 * {
 *   "result": true,
 *   "user": {
 *     "id": 576,
 *     "name": "Дмитрий Петров",
 *     "photo": "/avatars/ab/cd/ef/52461.jpg",
 *     "email": "my@email.com",
 *     "moderation": true,
 *     "moderationCount": 0,
 *     "settings": true
 *   }
 * }
 */
public class UserResultResponse extends ResultResponse{
    @JsonProperty("user")
    private final UserFullResponse userFullResponse;

    public UserResultResponse(boolean result, UserFullResponse userFullResponse) {
        super(result);
        this.userFullResponse = userFullResponse;
    }

    public UserFullResponse getUserFullResponse() {
        return userFullResponse;
    }
}
