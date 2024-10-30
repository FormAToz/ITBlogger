package main.api.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Формат ответа:
 *
 * {
 * 		"id": 88,
 * 		"name": "Дмитрий Петров",
 * 	    "photo": "/avatars/ab/cd/ef/52461.jpg"
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    protected long id;
    protected String name;
    protected String photo;

    public UserResponse() {
    }

    public UserResponse(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserResponse(long id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public long getId() {
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
