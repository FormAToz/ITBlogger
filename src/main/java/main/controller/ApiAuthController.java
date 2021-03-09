package main.controller;

import main.api.request.EmailRequest;
import main.api.request.auth.AuthorizationRequest;
import main.api.request.auth.LoginRequest;
import main.api.response.CaptchaResponse;
import main.api.response.result.ResultResponse;
import main.service.CaptchaService;
import main.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.security.Principal;

@RestController
@RequestMapping("api/auth")
public class ApiAuthController {
    private final UserService userService;
    private final CaptchaService captchaService;

    public ApiAuthController(UserService userService, CaptchaService captchaService) {
        this.userService = userService;
        this.captchaService = captchaService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResultResponse> register(@RequestBody AuthorizationRequest authorizationRequest) {
        return new ResponseEntity<>(userService.register(authorizationRequest), HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity<ResultResponse> authStatus(Principal principal) {
        return new ResponseEntity<>(userService.authStatus(principal), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ResultResponse> logIn(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(userService.logIn(loginRequest), HttpStatus.OK);
    }

    @GetMapping("/logout")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> logOut() {
        return new ResponseEntity<>(userService.logout(), HttpStatus.OK);
    }

    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restorePassword(@RequestBody EmailRequest emailRequest) throws MessagingException {
        return new ResponseEntity<>(userService.restorePassword(emailRequest), HttpStatus.OK);
    }

    @PostMapping("/password")
    public ResponseEntity<ResultResponse> changePassword(@RequestBody AuthorizationRequest authorizationRequest) {
        return new ResponseEntity<>(userService.changePassword(authorizationRequest), HttpStatus.OK);
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptchaCode() {
        return new ResponseEntity<>(captchaService.generateCaptcha(), HttpStatus.OK);
    }
}
