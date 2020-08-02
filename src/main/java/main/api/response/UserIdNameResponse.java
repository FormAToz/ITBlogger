package main.api.response;

/**
 * Формат ответа:
 *
 * {
 * 		"id": 88,
 * 		"name": "Дмитрий Петров"
 * }
 */
public class UserIdNameResponse {
    private int id;
    private String name;

    public UserIdNameResponse() {
    }

    public UserIdNameResponse(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
