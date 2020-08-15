package main.api.response;

/**
 * Формат ответа:
 *
 * {
 *   "secret": "car4y8cryaw84cr89awnrc",
 *   "image": "data:image/png;base64, код_изображения_в_base64"
 * }
 */
public class CaptchaResponse {
    private String secret;
    private String image;

    public CaptchaResponse(String secret, String image) {
        this.secret = secret;
        this.image = image;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
