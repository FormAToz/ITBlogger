package main.controller;

import main.Main;
import main.api.request.EmailRequest;
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

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class ApiAuthController {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");

    private final UserService userService;
    private final CaptchaService captchaService;

    public ApiAuthController(UserService userService, CaptchaService captchaService) {
        this.userService = userService;
        this.captchaService = captchaService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResultResponse> register(@RequestBody AuthorizationRequest authorizationRequest) {
        try {
            return new ResponseEntity<>(userService.register(authorizationRequest), HttpStatus.OK);

        } catch (InvalidParameterException e) {
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of(e.getType(), e.getMessage())), HttpStatus.OK);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<ResultResponse> authStatus(HttpServletRequest request) {
        try {
            return new ResponseEntity<>(userService.authStatus(), HttpStatus.OK);

        } catch (UserNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResultResponse> logIn(@RequestBody LoginRequest loginRequest) {
        try {
            return new ResponseEntity<>(userService.logIn(loginRequest), HttpStatus.OK);

        } catch (UserNotFoundException | InvalidParameterException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<ResultResponse> logOut() {
        return new ResponseEntity<>(userService.logout(), HttpStatus.OK);
    }

    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restorePassword(@RequestBody EmailRequest emailRequest) {
        try {
            return new ResponseEntity<>(userService.restorePassword(emailRequest), HttpStatus.OK);

        } catch (UserNotFoundException | MessagingException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
    }

    @PostMapping("/password")
    public ResponseEntity<ResultResponse> changePassword(@RequestBody AuthorizationRequest authorizationRequest) {
        try {
            return new ResponseEntity<>(userService.changePassword(authorizationRequest), HttpStatus.OK);

        } catch (InvalidParameterException e) {
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of(e.getType(), e.getMessage())), HttpStatus.OK);
        }
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptchaCode() {
        return new ResponseEntity<>(captchaService.generateCaptcha(), HttpStatus.OK);
    }
}
