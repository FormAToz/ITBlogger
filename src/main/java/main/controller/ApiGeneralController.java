package main.controller;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.tag.TagListResponse;
import main.service.InitService;
import main.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class ApiGeneralController {
    private final TagService tagService;
    private final InitService initService;

    public ApiGeneralController(TagService tagService, InitService initService) {
        this.tagService = tagService;
        this.initService = initService;
    }

    // Общие данные блога
    @GetMapping("/init")
    public ResponseEntity<InitResponse> init() {
        return new ResponseEntity<>(initService.init(), HttpStatus.OK);
    }

    // Получение настроек
    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> settings() {
        // TODO modify?
        SettingsResponse settings = new SettingsResponse(false, true, true);
        return new ResponseEntity<>(settings, HttpStatus.OK);
    }

    // Получение списка тэгов
    @GetMapping("/tag")
    public ResponseEntity<TagListResponse> tags() {
        return new ResponseEntity<>(tagService.tags(), HttpStatus.OK);
    }
}
