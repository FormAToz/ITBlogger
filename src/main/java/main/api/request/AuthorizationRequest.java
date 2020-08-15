package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Формат ответа:
 *
 * {
 * 	"e_mail":"konstantin@mail.ru",
 * 	"password":"123456",
 * 	"name":"Константин",
 * 	"captcha":"d34f",
 * 	"captcha_secret":"69sdFd67df7Pd9d3"
 * }
 */
public class AuthorizationRequest {
    private String name;
    private String code;
    private String password;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaSecret() {
        return captchaSecret;
    }

    public void setCaptchaSecret(String captchaSecret) {
        this.captchaSecret = captchaSecret;
    }
}
