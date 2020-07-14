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
    private final String title;
    private final String subtitle;
    private final String phone;
    private final String email;
    private final String copyright;
    private final String copyrightFrom;

    public InitResponse(String title, String subtitle, String phone, String email, String copyright, String copyrightFrom) {
        this.title = title;
        this.subtitle = subtitle;
        this.phone = phone;
        this.email = email;
        this.copyright = copyright;
        this.copyrightFrom = copyrightFrom;
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
