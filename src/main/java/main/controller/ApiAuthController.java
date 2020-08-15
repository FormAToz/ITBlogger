package main.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.api.request.AuthorizationRequest;
import main.api.response.CaptchaResponse;
import main.api.response.result.ResultResponse;
import main.service.AuthorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/auth")
public class ApiAuthController {
    private final AuthorizationService authorizationService;

    public ApiAuthController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    // Регистрация
    @PostMapping("/register")
    public ResponseEntity<ResultResponse> register(AuthorizationRequest authorizationRequest) {
        // TODO проверить параметры с фронта
        return authorizationService.register(authorizationRequest);
    }

    // Статус авторизации
    @GetMapping("/check")
    public ResponseEntity<ResultResponse> check(HttpServletRequest request) {
        return authorizationService.check(request);
    }

    // Вход
    @PostMapping("/login")
    public ResponseEntity<ResultResponse> logIn(@JsonProperty("e_mail") String eMail, String password, HttpServletRequest request) {
        // TODO проверить параметры с фронта
        return authorizationService.logIn(eMail, password, request);
    }

    // Выход
    @GetMapping("/logout")
    public ResponseEntity<ResultResponse> logout() {
        return authorizationService.logout();
    }

    // Восстановление пароля
    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restore(String email) {
        // TODO проверить параметры с фронта
        return authorizationService.restorePassword(email);
    }

    // Изменение пароля
    @PostMapping("/password")
    public ResponseEntity<ResultResponse> password(AuthorizationRequest authorizationRequest) {
        // TODO проверить параметры с фронта
        return authorizationService.changePassword(authorizationRequest);
    }

    // Запрос каптчи
    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() {
        return authorizationService.generateCaptcha();
    }
}
