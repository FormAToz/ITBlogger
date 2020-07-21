package main.controller;

        import main.api.response.UserFullResponse;
        import main.api.response.UserResultResponse;
        import main.service.UserService;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import javax.servlet.http.HttpServletRequest;

/**
 * Класс обрабатывает все запросы /api/auth/*
 * */
@RestController
@RequestMapping("api/auth")
public class ApiAuthController {
    private final UserService userService;

    public ApiAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("check")
    public ResponseEntity<UserResultResponse> check(HttpServletRequest request) {
        UserFullResponse user = new UserFullResponse();
        user.setName("Андрей Данилов");
        user.setId(1);
        user.setEmail("7.danilov@gmail.com");
        user.setModeration(true);
        user.setModerationCount(0);
        user.setSettings(true);

        String sessionId = request.getSession().getId();
        userService.saveUserIdFromSession(user.getId(), sessionId);
        UserResultResponse userResultResponse = new UserResultResponse(true, user);
        return new ResponseEntity<>(userResultResponse, HttpStatus.OK);
    }
}
