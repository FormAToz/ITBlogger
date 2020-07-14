package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class UserResultResponse {
    private final boolean result;
    @JsonProperty("user")
    private final UserFullResponse userFullResponse;

    public UserResultResponse(boolean result, UserFullResponse userFullResponse) {
        this.result = result;
        this.userFullResponse = userFullResponse;
    }

    public boolean isResult() {
        return result;
    }

    public UserFullResponse getUserFullResponse() {
        return userFullResponse;
    }
}
