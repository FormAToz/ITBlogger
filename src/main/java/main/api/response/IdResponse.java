package main.api.response;

/**
 * Формат успешного ответа:
 *
 * {
 * 	    "id": 345
 * }
 */
public class IdResponse {
    private final int id;

    public IdResponse(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
