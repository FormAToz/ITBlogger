package main.api.response.tag;

/**
 * Формат ответа:
 * {
 *    "name": "PHP",
 *    "weight": 1
 * }
 */
public class TagResponse {
    private final String name;
    private final float weight;

    public TagResponse(String name, float weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public float getWeight() {
        return weight;
    }
}
