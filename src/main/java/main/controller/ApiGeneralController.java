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
import main.exception.*;
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
    private final ImageService imageService;
    private final PostService postService;
    private final UserService userService;
    private final SettingsService settingsService;

    public ApiGeneralController(TagService tagService, ImageService imageService, PostService postService, UserService userService, SettingsService settingsService) {
        this.tagService = tagService;
        this.imageService = imageService;
        this.postService = postService;
        this.userService = userService;
        this.settingsService = settingsService;
    }

    // Общие данные блога
    @GetMapping("/init")
    public ResponseEntity<InitResponse> init() {
        return new ResponseEntity<>(settingsService.init(), HttpStatus.OK);
    }

    // Получение настроек
    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> getSettings() {
        try {
            return new ResponseEntity<>(settingsService.getSettings(), HttpStatus.OK);

        } catch (SettingNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Сохранение настроек
    @PutMapping("/settings")
    public void saveSettings(@RequestBody SettingsResponse settingsResponse) {
        try {
            settingsService.saveSettings(settingsResponse);

        } catch (SettingNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
        }
    }

    // Получение списка тэгов
    @GetMapping("/tag")
    public ResponseEntity<TagListResponse> tags() {
        return new ResponseEntity<>(tagService.tags(), HttpStatus.OK);
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

        } catch (UserNotFoundException | PostNotFoundException | CommentNotFoundException e) {
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
        return new ResponseEntity<>(postService.getAllPostsForCalendar(year), HttpStatus.OK);
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