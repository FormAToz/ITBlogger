package main.service;

import main.Main;
import main.api.request.AuthorizationRequest;
import main.api.response.StatisticsResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.api.response.result.UserResultResponse;
import main.api.response.user.UserFullResponse;
import main.api.response.user.UserResponse;
import main.exception.NoSuchTextLengthException;
import main.exception.UserNotFoundException;
import main.model.Post;
import main.model.User;
import main.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");

    private Map<String, Integer> userIdFromSession;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpServletRequest servletRequest;
    @Autowired
    private TextService textService;
    @Autowired
    private PostService postService;

    /**
     * Создание тестового юзера
     */
    public void createTestUser() {
        User u = new User();
        u.setName("Андрей Данилов");
        u.setEmail("7.danilov@gmail.com");
        u.setRegTime(LocalDateTime.now().plusHours(3));
        u.setPhoto("a/b/c.jpeg");
        u.setPassword("123");
        u.setIsModerator(1);
        userRepository.save(u);
    }

    public User getUserById(int id) throws UserNotFoundException{

        return userRepository.findById(id).orElseThrow(() -> {
            String message = "Пользователь с id = " + id + " не найден";

            LOGGER.info(MARKER, message);
            return new UserNotFoundException(message);
        });
    }

    public void saveUserIdFromSession(int userId, String sessionId) {
        userIdFromSession = new HashMap<>();
        userIdFromSession.put(sessionId, userId);
    }

    public User getUserFromSession() throws UserNotFoundException {
        String sessionId = servletRequest.getSession().getId();
        int userId = userIdFromSession.getOrDefault(sessionId, 0);

        if (userId == 0) {
            String message = "Пользователь не зарегестрирован в сессии - " + sessionId;

            LOGGER.info(MARKER, message);
            throw  new UserNotFoundException(message);
        }
        return getUserById(userId);
    }

    /**
     * Модератор = 1. Не модератор = 0
     */
    public boolean isNotModerator(User user) {
        return user.getIsModerator() == 0;
    }

    public boolean isNotAuthor(User user, Post post) {
        return user.getId() != post.getUser().getId();
    }

    /**
     * Метод возвращает статистику постов текущего авторизованного пользователя:
     * общие количества параметров для всех публикаций, у который он является автором и доступные для чтения.
     */
    public ResponseEntity<StatisticsResponse> getMyStatistics() {
        // TODO реализовать
        return new ResponseEntity<>(new StatisticsResponse(), HttpStatus.OK);
    }

    /**
     * Метод создаёт пользователя в базе данных, если введённые данные верны.
     * Если данные неверные - пользователь не создаётся, а метод возвращает соответствующую ошибку.
     *
     * e_mail         - e-mail пользователя
     * name           - имя пользователя
     * password       - пароль для аккаунта
     * captcha        - код капчи
     * captcha_secret - секретный код капчи
     */
    public ResultResponse register(AuthorizationRequest authorizationRequest) throws NoSuchTextLengthException {

        // TODO реализовать проверки введенных данных
        textService.checkPasswordLength(authorizationRequest.getPassword());

        if (false) {
            return new ErrorResultResponse(false, Map.of());
        }

        return new ResultResponse(true);
    }

    /**
     * Метод возвращает информацию о текущем авторизованном пользователе, если он авторизован.
     * Он должен проверять, сохранён ли идентификатор текущей сессии в списке авторизованных.
     * Значение moderationCount содержит количество постов необходимых для проверки модераторами.
     * Считаются посты имеющие статус NEW и не проверерны модератором. Если пользователь не модератор возращать 0 в moderationCount.
     */
    public ResponseEntity<ResultResponse> check(HttpServletRequest request) {
        if (true) {
            // TODO реализовать после входа, если пользователь авторизирован
            String sessionId = request.getSession().getId();

            saveUserIdFromSession(1, sessionId);

            return new ResponseEntity<>(new UserResultResponse(true,
                    new UserFullResponse(1, "Андрей Данилов")
                            .email("7.danilov@gmail.com")
                            .moderation(true)
                            .moderationCount(postService.countPostsForModeration())
                            .settings(true))
                    ,
                    HttpStatus.OK);
        }
        else {
            // TODO если пользователь не авторизирован
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Метод проверяет введенные данные и производит авторизацию пользователя, если введенные данные верны.
     * Если пользователь авторизован, идентификатор его сессии должен запоминаться в Map<String, Integer> со значением,
     * равным ID пользователя, которому принадлежит данная сессия.
     *
     * В параметрах объекта user выводятся имя пользователя, ссылка на его аватар, e-mail,
     * параметры moderation (если равен true, то у пользователя есть права на модерацию и в выпадающем меню
     * справа будет отображаться пункт меню Модерация с цифрой, указанной в параметре moderationCount) и
     * settings (если равен true, то пользователю доступны настройки блога).
     * Оба параметра - moderation и settings - должны быть равны true, если пользователь является модератором.
     *
     * Значение moderationCount содержит количество постов необходимых для проверки модераторами.
     * Считаются посты имеющие статус NEW и не проверерны модератором. Если пользователь не модератор возращать 0 в moderationCount.
     *
     * @param eMail - e-mail пользователя
     * @param password - пароль пользователя
     * @param request  - для проверки юзера и сессии
     */
    public ResponseEntity<ResultResponse> logIn(String eMail, String password, HttpServletRequest request) {
        if (true) {
            UserFullResponse userFullResponse = new UserFullResponse(0, "", "");
            // TODO реализовать в случае успешной авторизации
            System.out.println(eMail);
            System.out.println(password);

            return new ResponseEntity<>(new UserResultResponse(true, userFullResponse), HttpStatus.OK);
        }
        else {
            // TODO в случае ошибки
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Метод разлогинивает пользователя: удаляет идентификатор его сессии из списка авторизованных.
     * Всегда возвращает true, даже если идентификатор текущей сессии не найден в списке авторизованных.
     */
    public ResponseEntity<ResultResponse> logout() {
        //TODO
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    /**
     * Метод проверяет наличие в базе пользователя с указанным e-mail.
     * Если пользователь найден, ему должно отправляться письмо со ссылкой на восстановление пароля следующего вида - /login/change-password/HASH,
     * где HASH - сгенерированный код вида b55ca6ea6cb103c6384cfa366b7ce0bdcac092be26bc0
     * (код должен генерироваться случайным образом и сохраняться в базе данных в поле users.code).
     *
     * @param email - e-mail пользователя
     */
    public ResponseEntity<ResultResponse> restorePassword(String email) {
        if (true) {
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
    }

    /**
     * Метод проверяет корректность кода восстановления пароля (параметр code) и корректность кодов капчи:
     * введённый код (параметр captcha) должен совпадать со значением в поле code таблицы captcha_codes,
     * соответствующем пришедшему значению секретного кода (параметр captcha_secret и поле secret_code в таблице базы данных).
     *
     * code - код восстановления пароля
     * password - новый пароль
     * captcha - код капчи
     * captcha_secret - секретный код капчи
     */
    public ResponseEntity<ResultResponse> changePassword(AuthorizationRequest authorizationRequest) {
        if (true) {
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }
        else {
            Map<String, String> errors = new HashMap<>();

            return new ResponseEntity<>(new ErrorResultResponse(false, errors), HttpStatus.OK);
        }
    }

    /**
     * Метод обрабатывает информацию, введённую пользователем в форму редактирования своего профиля.
     * Если пароль не введён, его не нужно изменять. Если введён, должна проверяться его корректность: достаточная длина.
     * Одинаковость паролей, введённых в двух полях, проверяется на frontend - на сервере проверка не требуется.
     *
     * Запрос без изменения пароля и фотографии: Content-Type: application/json
     *
     * {
     *   "name":"Sendel",
     *   "email":"sndl@mail.ru"
     * }
     * Запрос c изменением пароля и без изменения фотографии: Content-Type: application/json
     *
     * {
     *   "name":"Sendel",
     *   "email":"sndl@mail.ru",
     *   "password":"123456"
     * }
     * Запрос c изменением пароля и фотографии: Content-Type: multipart/form-data;
     *
     * {
     *   "photo": <binary_file>,
     *   "name":"Sendel",
     *   "email":"sndl@mail.ru",
     *   "password":"123456",
     *   "removePhoto":0
     * }
     * ⚠️ при загрузке файла изображения фотографии пользователя, необходимо выполнять обрезку и изменение размера фотографии до 36х36 пикселей.
     *
     * Запрос на удаление фотографии без изменения пароля: Content-Type: application/json
     *
     * {
     *   "name":"Sendel",
     *   "email":"sndl@mail.ru",
     *   "removePhoto":1,
     *   "photo": ""
     * }
     *
     * photo - файл с фото или пустое значение (если его требуется удалить)
     * removePhoto - параметр, который указывает на то, что фотографию нужно удалить (если значение равно 1)
     * name - новое имя
     * email - новый e-mail
     * password - новый пароль
     */
    public ResponseEntity<ResultResponse> editMyProfile() {
        if (true) {
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }
        else {
            Map<String, String> errors = new HashMap<>();

            return new ResponseEntity<>(new ErrorResultResponse(false, errors), HttpStatus.OK);
        }
    }
}
