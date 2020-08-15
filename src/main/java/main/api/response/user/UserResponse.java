package main.api.response.user;

/**
 * Формат ответа:
 *
 * {
 * 		"id": 88,
 * 		"name": "Дмитрий Петров",
 * 	    "photo": "/avatars/ab/cd/ef/52461.jpg"
 * }
 */
public class UserResponse {
    protected int id;
    protected String name;
    protected String photo;

    public UserResponse() {
    }

    public UserResponse(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserResponse(int id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
