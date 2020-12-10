package main.controller;

import main.Main;
import main.api.request.auth.AuthorizationRequest;
import main.api.request.auth.LoginRequest;
import main.api.response.CaptchaResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.exception.InvalidParameterException;
import main.exception.UserNotFoundException;
import main.service.CaptchaService;
import main.service.SettingsService;
import main.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class ApiAuthController {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");

    private final UserService userService;
    private final CaptchaService captchaService;
    private final SettingsService settingsService;

    public ApiAuthController(UserService userService, CaptchaService captchaService, SettingsService settingsService) {
        this.userService = userService;
        this.captchaService = captchaService;
        this.settingsService = settingsService;
    }

    // Регистрация
    @PostMapping("/register")
    public ResponseEntity<ResultResponse> register(@RequestBody AuthorizationRequest authorizationRequest) {
        try {
            return new ResponseEntity<>(userService.register(authorizationRequest), HttpStatus.OK);

        } catch (InvalidParameterException e) {
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of(e.getType(), e.getMessage())), HttpStatus.OK);
        }
    }

    // Статус авторизации
    @GetMapping("/check")
    public ResponseEntity<ResultResponse> authStatus(HttpServletRequest request) {
        try {
            return new ResponseEntity<>(userService.authStatus(), HttpStatus.OK);

        } catch (UserNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
    }

    // Вход
    @PostMapping("/login")
    public ResponseEntity<ResultResponse> logIn(@RequestBody LoginRequest loginRequest) {
        try {
            return new ResponseEntity<>(userService.logIn(loginRequest), HttpStatus.OK);

        } catch (UserNotFoundException | InvalidParameterException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
    }

    // Выход
    @GetMapping("/logout")
    public ResponseEntity<ResultResponse> logout() {
        return new ResponseEntity<>(userService.logout(), HttpStatus.OK);
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
