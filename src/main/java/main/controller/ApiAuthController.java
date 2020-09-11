package main.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.api.request.AuthorizationRequest;
import main.api.response.CaptchaResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.exception.NoSuchTextLengthException;
import main.service.CaptchaService;
import main.service.TextService;
import main.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class ApiAuthController {
    private final UserService userService;
    private final CaptchaService captchaService;
    private final TextService textService;

    public ApiAuthController(UserService userService, CaptchaService captchaService, TextService textService) {
        this.userService = userService;
        this.captchaService = captchaService;
        this.textService = textService;
    }

    // Регистрация
    @PostMapping("/register")
    public ResponseEntity<ResultResponse> register(@RequestBody AuthorizationRequest authorizationRequest) {
        ResultResponse resultResponse = null;

        // TODO проверить параметры с фронта, статусы ответа в случае ошибки
        System.out.printf("Registration:%n name: %s%n, code: %s%n, password: %s%n, captcha: %s%n, captcha_secret: %s%n",
                authorizationRequest.getName(), authorizationRequest.getCode(), authorizationRequest.getPassword(),
                authorizationRequest.getCaptcha(), authorizationRequest.getCaptchaSecret());

        try {
            resultResponse = userService.register(authorizationRequest);

        } catch (NoSuchTextLengthException e) {
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of(e.getType(), e.getMessage())), HttpStatus.OK);
        }
        return new ResponseEntity<>(resultResponse, HttpStatus.OK);
    }

    // Статус авторизации
    @GetMapping("/check")
    public ResponseEntity<ResultResponse> check(HttpServletRequest request) {
        return userService.check(request);
    }

    // Вход
    @PostMapping("/login")
    public ResponseEntity<ResultResponse> logIn(@JsonProperty("e_mail") String eMail, String password, HttpServletRequest request) {
        // TODO проверить параметры с фронта
        return userService.logIn(eMail, password, request);
    }

    // Выход
    @GetMapping("/logout")
    public ResponseEntity<ResultResponse> logout() {
        return userService.logout();
    }

    // Восстановление пароля
    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restore(String email) {
        // TODO проверить параметры с фронта
        return userService.restorePassword(email);
    }

    // Изменение пароля
    @PostMapping("/password")
    public ResponseEntity<ResultResponse> password(@RequestBody AuthorizationRequest authorizationRequest) {
        // TODO проверить параметры с фронта
        return userService.changePassword(authorizationRequest);
    }

    // Запрос каптчи
    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() {
        return new ResponseEntity<>(captchaService.generateCaptcha(), HttpStatus.OK);
    }
}
