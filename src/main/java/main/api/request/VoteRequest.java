package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Класс запроса лайка/дизлайка
 *
 * Формат запроса:
 *
 * {
 *   "post_id": 151
 * }
 */
public class VoteRequest {
    @JsonProperty(value = "post_id")
    private int postId;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
