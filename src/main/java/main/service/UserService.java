package main.service;

import main.Main;
import main.api.request.auth.AuthorizationRequest;
import main.api.request.auth.LoginRequest;
import main.api.response.StatisticsResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.api.response.result.UserResultResponse;
import main.api.response.user.UserFullResponse;
import main.exception.InvalidParameterException;
import main.exception.PostNotFoundException;
import main.exception.UserNotFoundException;
import main.model.Post;
import main.model.User;
import main.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");

    private Map<String, Integer> userIdFromSession = new HashMap<>();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpServletRequest servletRequest;
    @Autowired
    private TextService textService;
    @Autowired
    private PostService postService;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private TimeService timeService;
    @Autowired
    private VoteService voteService;

    /**
     * Создание тестового юзера
     */
    public void createTestUser() {
        // TODO удалить
        User u = new User();
        u.setName("Андрей Данилов");
        u.setEmail("7.danilov@gmail.com");
        u.setRegTime(LocalDateTime.now().plusHours(3));
        u.setPhoto("a/b/c.jpeg");
        u.setPassword("123");
        u.setIsModerator(1);
        userRepository.save(u);
    }

    /**
     * Метод получения пользователя из репозитория
     *
     * @param id - Id пользователя
     * @return   - User
     * @throws UserNotFoundException - в случае, если пользователь не найден
     */
    public User getUserById(int id) throws UserNotFoundException{
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
    }

    /**
     * Метод сохраняет отображение Id пользователя/Id сессии
     *
     * @param userId - Id пользователя
     * @param sessionId - Id сессии
     */
    public void saveUserIdFromSession(int userId, String sessionId) {
        userIdFromSession.put(sessionId, userId);
    }

    /**
     * Метод получения Id пользователя из сессии
     *
     * @return - User
     * @throws UserNotFoundException - в случае, если пользователь не зарегестрирован в сессии
     */
    public User getUserFromSession() throws UserNotFoundException {
        String sessionId = servletRequest.getSession().getId();
        int userId = userIdFromSession.getOrDefault(sessionId, 0);

        if (userId == 0) {
            throw new UserNotFoundException("Пользователь не зарегестрирован в сессии - " + sessionId);
        }
        return getUserById(userId);
    }

    /**
     * Метод проверки, является ли пользователь модератором
     * Модератор = 1. Не модератор = 0
     *
     * @param user - пользователь
     * @return - true или false
     */
    public boolean isModerator(User user) {
        return user.getIsModerator() == 1;
    }

    /**
     * Метод проверки, является ли пользователь автором
     * @param user - пользователь
     * @param post - пост
     * @return - true или false
     */
    public boolean isAuthor(User user, Post post) {
        return user.getId() == post.getUser().getId();
    }

    /**
     * Метод возвращает статистику постов текущего авторизованного пользователя:
     * общие количества параметров для всех публикаций, у который он является автором и доступные для чтения.
     * @return - StatisticsResponse
     */
    public StatisticsResponse getMyStatistics() throws UserNotFoundException, InvalidParameterException {
        User user = getUserFromSession();
        LocalDateTime timeOfFirstPost = postService.getTimeOfFirstPostByUser(user);

        return new StatisticsResponse()
                .postsCount(postService.countPostsByUser(user))
                .likesCount(voteService.countLikesByUser(user))
                .dislikesCount(voteService.countDislikesByUser(user))
                .viewsCount(postService.countViewsFromPostsByUser(user))
                .firstPublication(timeService.getTimestampFromLocalDateTime(timeOfFirstPost));
    }

    /**
     * Метод создаёт пользователя в базе данных, если введённые данные верны.
     * Если данные неверные - пользователь не создаётся, а метод возвращает соответствующую ошибку.
     *

     * @param authRequest - AuthorizationRequest:
     *                                  e_mail         - e-mail пользователя
     *                                  name           - имя пользователя
     *                                  password       - пароль для аккаунта
     *                                  captcha        - код капчи
     *                                  captcha_secret - секретный код капчи
     * @return - ResultResponse
     * @throws InvalidParameterException - в случае ошибок с текстом
     */
    public ResultResponse register(AuthorizationRequest authRequest) throws InvalidParameterException {
        textService.checkEmailForCorrect(authRequest.getEmail());  // Проверка e-mail

        if (userRepository.existsByEmailIgnoreCase(authRequest.getEmail())) {
            throw new InvalidParameterException("email", "Этот e-mail уже зарегистрирован");
        }

        textService.checkNameForCorrect(authRequest.getName());    // Проверка корректности имени
        textService.checkPasswordLength(authRequest.getPassword());    // Проверка длины пароля
        captchaService.checkCaptcha(authRequest.getCaptcha(), authRequest.getCaptchaSecret());   // Проверка корректности ввода капчи

        User user = new User();     // Создаем и сохраняем пользователя
        user.setName(authRequest.getName());
        user.setEmail(authRequest.getEmail());
        user.setPassword(encodePassword(authRequest.getPassword()));
        user.setRegTime(LocalDateTime.now());
        userRepository.save(user);

        LOGGER.info(MARKER, "Пользователь (Имя: {}, E-mail: {}) успешно зарегестрирован",
                authRequest.getName(), authRequest.getEmail());

        return new ResultResponse(true);
    }

    /**
     * Метод кодирования пароля
     *
     * @param password - незакодированный пароль
     * @return - закодированный пароль
     */
    private String encodePassword(String password) {
        byte[] pwBytes = DigestUtils.md5Digest(password.getBytes());

        return Base64.encodeBase64String(pwBytes);
    }

    /**
     * Метод возвращает информацию о текущем авторизованном пользователе, если он авторизован.
     * Он должен проверять, сохранён ли идентификатор текущей сессии в списке авторизованных.
     * Значение moderationCount содержит количество постов необходимых для проверки модераторами.
     * Считаются посты имеющие статус NEW и не проверерны модератором. Если пользователь не модератор возращать 0 в moderationCount.
     */
    public ResultResponse check() throws UserNotFoundException {
        return new UserResultResponse(true, migrateToUserFullResponse(getUserFromSession()));
    }

    /**
     * Метод проверяет введенные данные и производит авторизацию пользователя, если введенные данные верны.
     * Если пользователь авторизован, идентификатор его сессии должен запоминаться в Map<String, Integer> со значением,
     * равным ID пользователя, которому принадлежит данная сессия.
     *
     * Новый пользователь регистрируется не как модератор и не может менять настройки блога.
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
     * @param loginRequest - e-mail и пароль пользователя
     * @return - ResultResponse
     */
    public ResultResponse logIn(LoginRequest loginRequest) throws UserNotFoundException, InvalidParameterException {
        // ищем пользователя по имейл
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с E-mail: " + loginRequest.getEmail() + " не зарегистрирован"));

        // сверяем пароль
        if (!user.getPassword().equals(encodePassword(loginRequest.getPassword()))) {
            throw new InvalidParameterException("Введен некорректный пароль");
        }

        // сохраняем отображение пользователь/сессия
        saveUserIdFromSession(user.getId(), servletRequest.getSession().getId());

        return new UserResultResponse(true, migrateToUserFullResponse(user));
    }

    /**
     * Метод преобразования User в UserFullResponse
     * @param user - пользователь из репозитория
     * @return - UserFullResponse для ответа на фронт
     */
    private UserFullResponse migrateToUserFullResponse(User user) {
        return new UserFullResponse()
                .id(user.getId())
                .name(user.getName())
                .photo(user.getPhoto())
                .email(user.getEmail())
                .moderation(isModerator(user))
                .moderationCount(postService.countPostsForModeration())
                .settings(isModerator(user));
    }

    /**
     * Метод разлогинивает пользователя: удаляет идентификатор его сессии из списка авторизованных.
     * Всегда возвращает true, даже если идентификатор текущей сессии не найден в списке авторизованных.
     */
    public ResultResponse logout() {
        userIdFromSession.clear();
        return new ResultResponse(true);
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
        // TODO
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
