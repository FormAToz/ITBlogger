package main.controller;

        import main.api.response.UserFullResponse;
        import main.api.response.UserResultResponse;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class ApiAuthController {
    /**
     * Класс обрабатывает все запросы /api/auth/*
     * */

    @GetMapping("check")
    public ResponseEntity<UserResultResponse> check() {
        UserFullResponse user = new UserFullResponse();
        user.setName("Андрей Данилов");
        user.setId(1);
        user.setEmail("7.danilov@gmail.com");
        user.setModeration(true);
        user.setModerationCount(0);
        user.setSettings(true);

        UserResultResponse userResultResponse = new UserResultResponse(true, user);
        return new ResponseEntity<>(userResultResponse, HttpStatus.OK);
    }
}
