package main.api.response;

/**
 * Формат ответа:
 *
 * {
 * 	"title": "DevPub",
 * 	"subtitle": "Рассказы разработчиков",
 * 	"phone": "+7 903 666-44-55",
 * 	"email": "mail@mail.ru",
 * 	"copyright": "Дмитрий Сергеев",
 * 	"copyrightFrom": "2005"
 * }
 */
public class InitResponse {
    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    private String copyrightFrom;

    public InitResponse() {
    }

    public InitResponse title(String title) {
        this.title = title;
        return this;
    }

    public InitResponse subtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public InitResponse phone(String phone) {
        this.phone = phone;
        return this;
    }

    public InitResponse email(String email) {
        this.email = email;
        return this;
    }

    public InitResponse copyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    public InitResponse copyrightFrom(String copyrightFrom) {
        this.copyrightFrom = copyrightFrom;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getCopyrightFrom() {
        return copyrightFrom;
    }
}
