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

    public UserFullResponse() {
    }

    public UserFullResponse(int id, String name) {
        super(id, name);
    }

    public UserFullResponse(int id, String name, String photo) {
        super(id, name, photo);
    }

    public UserFullResponse email(String email) {
        this.email = email;
        return this;
    }

    public UserFullResponse moderation(boolean moderation) {
        this.moderation = moderation;
        return this;
    }

    public UserFullResponse moderationCount(int moderationCount) {
        this.moderationCount = moderationCount;
        return this;
    }

    public UserFullResponse settings(boolean settings) {
        this.settings = settings;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public boolean isModeration() {
        return moderation;
    }

    public int getModerationCount() {
        return moderationCount;
    }

    public boolean isSettings() {
        return settings;
    }
}
