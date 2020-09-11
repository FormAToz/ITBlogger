package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**\
 * Класс запроса с формы регистрации
 *
 * {
 *   "post_id":31,
 *   "decision":"accept"
 * }
 */
public class ModerationRequest {
    @JsonProperty("post_id")
    private final int postId;
    private final String decision;

    public ModerationRequest(int postId, String decision) {
        this.postId = postId;
        this.decision = decision;
    }

    public int getPostId() {
        return postId;
    }

    public String getDecision() {
        return decision;
    }
}
