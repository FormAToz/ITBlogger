package main.api.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Класс запроса с формы регистрации:
 *
 * {
 * 	"e_mail":"konstantin@mail.ru",
 * 	"code":"b55ca6ea6cb103c6384c",
 * 	"password":"123456",
 * 	"name":"Константин",
 * 	"captcha":"d34f",
 * 	"captcha_secret":"69sdFd67df7Pd9d3"
 * }
 */
public class AuthorizationRequest extends LoginRequest{
    private String name;
    private String code;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;

    public AuthorizationRequest() {
    }

    public AuthorizationRequest email(String email) {
        this.email = email;
        return this;
    }

    public AuthorizationRequest password(String password) {
        this.password = password;
        return this;
    }

    public AuthorizationRequest name(String name) {
        this.name = name;
        return this;
    }

    public AuthorizationRequest code(String code) {
        this.code = code;
        return this;
    }

    public AuthorizationRequest captcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public AuthorizationRequest captchaSecret(String captchaSecret) {
        this.captchaSecret = captchaSecret;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getCaptcha() {
        return captcha;
    }

    public String getCaptchaSecret() {
        return captchaSecret;
    }
}
