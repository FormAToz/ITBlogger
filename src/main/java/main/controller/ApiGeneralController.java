package main.controller;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.TagListResponse;
import main.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Класс для прочих запросов к API
 * */
@RestController
@RequestMapping("api")
public class ApiGeneralController {
    private final TagService tagService;

    public ApiGeneralController(TagService tagService) {
        this.tagService = tagService;
    }

    // Общие данные блога
    @GetMapping("init")
    public ResponseEntity<InitResponse> init() {
        InitResponse initResponse = new InitResponse(
                "IT-Blogger",
                "Рассказы разработчиков",
                "+7 999 777-77-77",
                "7.danilov@gmail.com",
                "Андрей Данилов",
                "2020");
        return new ResponseEntity<>(initResponse, HttpStatus.OK);
    }

    // Получение настроек
    @GetMapping("settings")
    public ResponseEntity<SettingsResponse> settings() {
        SettingsResponse settings = new SettingsResponse(false, true, true);
        return new ResponseEntity<>(settings, HttpStatus.OK);
    }

    // Получение списка тэгов
    @GetMapping("tag")
    public ResponseEntity<TagListResponse> tags() {
        return new ResponseEntity<>(tagService.tags(), HttpStatus.OK);
    }
}
