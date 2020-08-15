package main.controller;

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

    /**
     * Метод возвращает информацию о текущем авторизованном пользователе, если он авторизован.
     */
    @GetMapping("/check")
    public ResponseEntity<ResultResponse> check(HttpServletRequest request) {
        return authorizationService.check(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ResultResponse> logIn(String e_mail, String password, HttpServletRequest request) {
        return authorizationService.logIn(e_mail, password, request);
    }
}
