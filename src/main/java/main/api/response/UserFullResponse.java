package main.api.response;

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
public class UserFullResponse extends UserIdNameResponse{
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;

    public int getId() {
        return super.getId();
    }

    public void setId(int id) {
        super.setId(id);
    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
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
