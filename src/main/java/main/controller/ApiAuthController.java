package main.controller;

import org.json.simple.JSONObject;
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
    public JSONObject check() {
        JSONObject jObj = new JSONObject();
        JSONObject jUser = new JSONObject();
        jUser.put("id", 1);
        jUser.put("name", "Андрей Данилов");
        jUser.put("photo", "/avatars/ab/cd/ef/52461.jpg");
        jUser.put("email", "7.danilov@gmail.com");
        jUser.put("moderation", true);
        jUser.put("moderationCount", 56);
        jUser.put("settings", true);
        jObj.put("result", true);
        jObj.put("user", jUser);
        return jObj;
    }
}
