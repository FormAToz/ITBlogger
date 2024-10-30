package main.api.response;

/**
 * Формат успешного ответа:
 *
 * {
 * 	    "id": 345
 * }
 */
public class IdResponse {
    private final long id;

    public IdResponse(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
