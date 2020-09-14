package main.api.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Класс запроса с формы авторизации:
 *
 * {
 *   "e_mail":"my@email.com",
 *   "password":"dHdf6dDHfd"
 * }
 */
public class LoginRequest {
    @JsonProperty("e_mail")
    protected String email;
    protected String password;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
