package main.api.response.tag;

import java.util.List;

/**
 * Формат ответа:
 *
 * {
 *   "tags":
 *   [
 *    {
 *      "name": "PHP",
 *      "weight": 1
 *    },
 *    {
 *      "name": "Python",
 *      "weight": 0.33
 *    }
 *   ]
 * }
 */
public class TagListResponse {
    private final List<TagResponse> tags;

    public TagListResponse(List<TagResponse> tags) {
        this.tags = tags;
    }

    public List<TagResponse> getTags() {
        return tags;
    }
}
