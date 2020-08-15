package main.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.api.request.CommentRequest;
import main.api.response.CalendarResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.StatisticsResponse;
import main.api.response.result.ResultResponse;
import main.api.response.tag.TagListResponse;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api")
public class ApiGeneralController {
    private final TagService tagService;
    private final InitService initService;
    private final ImageService imageService;
    private final CommentService commentService;
    private final PostService postService;
    private final AuthorizationService authorizationService;
    private final UserService userService;

    public ApiGeneralController(TagService tagService, InitService initService, ImageService imageService, CommentService commentService, PostService postService, AuthorizationService authorizationService, UserService userService) {
        this.tagService = tagService;
        this.initService = initService;
        this.imageService = imageService;
        this.commentService = commentService;
        this.postService = postService;
        this.authorizationService = authorizationService;
        this.userService = userService;
    }

    // Общие данные блога
    @GetMapping("/init")
    public ResponseEntity<InitResponse> init() {
        return new ResponseEntity<>(initService.init(), HttpStatus.OK);
    }

    // Получение настроек
    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> getSettings() {
        // TODO modify? Метод возвращает глобальные настройки блога из таблицы global_settings.
        SettingsResponse settings = new SettingsResponse(false, true, true);
        return new ResponseEntity<>(settings, HttpStatus.OK);
    }

    // Сохранение настроек
    @PostMapping("/settings")
    public void saveSettings(SettingsResponse settingsResponse) {
        // TODO modify? С фронта приходит реквест, а не респонс. Сохранить настройки в таблицу global_settings
    }


    // Получение списка тэгов
    @GetMapping("/tag")
    public ResponseEntity<TagListResponse> tags() {
        return tagService.tags();
    }

    // Загрузка изображений
    @PostMapping(value = "/image", consumes = {"multipart/form-data"})
    public ResponseEntity loadImage(MultipartFile image) {
        // TODO разобраться со значением с фронта и откуда приходят изображения
        return imageService.loadImage(image);
    }

    // Отправка комментария к посту
    @PostMapping("/comment")
    public ResponseEntity comment(CommentRequest commentRequest) {
        // TODO проверить заполняется ли объект запроса
        return commentService.addComment(commentRequest);
    }

    // Модерация поста
    @PostMapping("/moderation")
    public ResponseEntity<ResultResponse> moderation(@JsonProperty("post_id") int postId, String decision) {
        // TODO проверить данные с фронта
        return postService.moderate(postId, decision);
    }

    // Календарь (количество публикаций)
    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> calendar(int year) {
        // TODO проверить данные с фронта
        return postService.getAllPostsForCalendar(year);
    }

    // Редактирование моего профиля
    @PostMapping("/profile/my")
    public ResponseEntity<ResultResponse> editMyProfile(/* TODO Разобраться с данными с фронта*/) {
        // TODO проверить параметры с фронта
        return authorizationService.editMyProfile();
    }

    // Моя статистика
    @GetMapping("/statistics/my")
    public ResponseEntity<StatisticsResponse> myStatistics() {
        return userService.getMyStatistics();
    }

    // Статистика по всему блогу
    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticsResponse> allStatistics() {
        return postService.getGlobalStatistics();
    }
}