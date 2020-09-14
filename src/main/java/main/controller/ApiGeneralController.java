package main.controller;

import main.Main;
import main.api.request.CommentRequest;
import main.api.request.ModerationRequest;
import main.api.response.CalendarResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.StatisticsResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.api.response.tag.TagListResponse;
import main.exception.InvalidParameterException;
import main.exception.PostNotFoundException;
import main.exception.UserNotFoundException;
import main.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("api")
public class ApiGeneralController {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");

    private final TagService tagService;
    private final InitService initService;
    private final ImageService imageService;
    private final PostService postService;
    private final UserService userService;

    public ApiGeneralController(TagService tagService, InitService initService, ImageService imageService, PostService postService, UserService userService) {
        this.tagService = tagService;
        this.initService = initService;
        this.imageService = imageService;
        this.postService = postService;
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
    public ResponseEntity<?> comment(@RequestBody CommentRequest commentRequest) {
        try {
            return new ResponseEntity<>(postService.addComment(commentRequest), HttpStatus.OK);

        } catch (UserNotFoundException | PostNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (InvalidParameterException e) {
            return new ResponseEntity<>(new ErrorResultResponse(false,
                            Map.of(e.getType(), e.getMessage())), HttpStatus.OK);
        }
    }

    // Модерация поста
    @PostMapping("/moderation")
    public ResponseEntity<ResultResponse> moderation(@RequestBody ModerationRequest request) {
        try {
            postService.moderate(request.getPostId(), request.getDecision());
        }
        catch (UserNotFoundException | PostNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
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
        return userService.editMyProfile();
    }

    // Моя статистика
    @GetMapping("/statistics/my")
    public ResponseEntity<StatisticsResponse> myStatistics() {
        return new ResponseEntity<>(userService.getMyStatistics(), HttpStatus.OK);
    }

    // Статистика по всему блогу
    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticsResponse> allStatistics() {
        return postService.getGlobalStatistics();
    }
}