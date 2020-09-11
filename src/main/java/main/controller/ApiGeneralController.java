package main.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mysql.cj.x.protobuf.Mysqlx;
import main.api.request.CommentRequest;
import main.api.request.ModerationRequest;
import main.api.response.*;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.api.response.tag.TagListResponse;
import main.exception.PostNotFoundException;
import main.exception.NoSuchTextLengthException;
import main.exception.UserNotFoundException;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("api")
public class ApiGeneralController {
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
        IdResponse idResponse = null;

        try {
            idResponse = postService.addComment(commentRequest);

        } catch (UserNotFoundException | PostNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchTextLengthException e) {
            return new ResponseEntity<ErrorResultResponse>(
                    new ErrorResultResponse(
                            false,
                            Map.of(e.getType(), "Текст комментария не задан или слишком короткий")), HttpStatus.OK);
        }
        return new ResponseEntity<IdResponse>(idResponse, HttpStatus.OK);
    }

    // Модерация поста
    @PostMapping("/moderation")
    public ResponseEntity<ResultResponse> moderation(@RequestBody ModerationRequest request) {
        try {
            postService.moderate(request.getPostId(), request.getDecision());
        }
        catch (UserNotFoundException | PostNotFoundException e) {
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
        return userService.getMyStatistics();
    }

    // Статистика по всему блогу
    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticsResponse> allStatistics() {
        return postService.getGlobalStatistics();
    }
}