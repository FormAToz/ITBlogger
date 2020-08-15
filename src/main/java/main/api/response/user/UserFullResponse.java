package main.api.response.user;

/**
 * Форматы ответов:
 *
 * {
 *    "id": 576,
 *    "name": "Дмитрий Петров",
 *    "photo": "/avatars/ab/cd/ef/52461.jpg",
 *    "email": "my@email.com",
 *    "moderation": true,
 *    "moderationCount": 0,
 *    "settings": true
 * }
 */
public class UserFullResponse extends UserResponse {
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isModeration() {
        return moderation;
    }

    public void setModeration(boolean moderation) {
        this.moderation = moderation;
    }

    public int getModerationCount() {
        return moderationCount;
    }

    public void setModerationCount(int moderationCount) {
        this.moderationCount = moderationCount;
    }

    public boolean isSettings() {
        return settings;
    }

    public void setSettings(boolean settings) {
        this.settings = settings;
    }
}
