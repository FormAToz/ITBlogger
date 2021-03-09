package main.service;

import main.Main;
import main.api.request.EmailRequest;
import main.api.request.ProfileRequest;
import main.api.request.auth.AuthorizationRequest;
import main.api.request.auth.LoginRequest;
import main.api.response.StatisticsResponse;
import main.api.response.result.ResultResponse;
import main.api.response.result.UserResultResponse;
import main.api.response.user.UserFullResponse;
import main.exception.InvalidParameterException;
import main.model.Post;
import main.model.User;
import main.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");

    @Autowired
    private UserRepository userRepository;
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
    @Autowired
    private ImageService imageService;
    @Autowired
    private MailService mailService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${photo.delete-value}")
    private int photoDeleteValue;
    @Value("${user.moderator-value}")
    private int moderatorValue;
    @Value("${root-domain}")
    private String rootDomain;
    @Value("${change-password-subaddress}")
    private String changePasswordSubAddress;
    @Value("${restore-password-subaddress}")
    private String restorePasswordSubAddress;

    /**
     * Метод получения пользователя из репозитория
     *
     * @param id - Id пользователя
     * @return - User
     * @throws UsernameNotFoundException - в случае, если пользователь не найден
     */
    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с id = " + id + " не найден"));
    }

    /**
     * Метод получения пользователя по e-mail
     *
     * @param email адрес электронной почты
     * @return объект User
     * @throws UsernameNotFoundException в случае, если пользователя с данным e-mail не существует
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с E-mail: " + email + " не зарегистрирован"));
    }

    /**
     * Метод получения пользователя по коду восстановления пароля.
     *
     * @param code код восстановления
     * @return User (пользователя, у которого есть этот код)
     * @throws InvalidParameterException в случае если пользователь не найден по коду
     */
    public User getUserByRecoveryCode(String code) {
        return userRepository.getUserByCode(code).orElseThrow(() ->
                new InvalidParameterException("code", "Ссылка для восстановления пароля устарела. <a href=\"" +
                        restorePasswordSubAddress + "\">Запросить ссылку снова</a>"));
    }

    /**
     * Метод получения авторизированного пользователя
     *
     * @return - User, авторизированный пользователь
     */
    public User getLoggedUser() {
        //TODO если пользователь не залогинился, то получаем 403 ошибку
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        return getUserByEmail(loggedUserEmail);
    }

    /**
     * Метод проверки, является ли пользователь модератором
     * Модератор = 1. Не модератор = 0
     *
     * @param user - пользователь
     * @return - true или false
     */
    public boolean isModerator(User user) {
        return user.getIsModerator() == moderatorValue;
    }

    /**
     * Метод проверки, является ли пользователь автором
     *
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
     *
     * @return - StatisticsResponse
     */
    public StatisticsResponse getMyStatistics() {
        User user = getLoggedUser();
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
     *                    e_mail         - e-mail пользователя
     *                    name           - имя пользователя
     *                    password       - пароль для аккаунта
     *                    captcha        - код капчи
     *                    captcha_secret - секретный код капчи
     * @return - ResultResponse
     * @throws InvalidParameterException - в случае ошибок с текстом
     */
    public ResultResponse register(AuthorizationRequest authRequest) {
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
        return new BCryptPasswordEncoder(12).encode(password);
    }

    /**
     * Метод возвращает информацию о текущем авторизованном пользователе, если он авторизован.
     * Он должен проверять, сохранён ли идентификатор текущей сессии в списке авторизованных.
     * Значение moderationCount содержит количество постов необходимых для проверки модераторами.
     * Считаются посты имеющие статус NEW и не проверерны модератором. Если пользователь не модератор возращать 0 в moderationCount.
     */
    public ResultResponse authStatus(Principal principal) {
        if (principal == null) {
            return new ResultResponse(false);
        }
        return new UserResultResponse(true, migrateToUserFullResponse(getLoggedUser()));
    }

    /**
     * Метод проверяет введенные данные и производит авторизацию пользователя, если введенные данные верны.
     * Если пользователь авторизован, идентификатор его сессии должен запоминаться в Map<String, Integer> со значением,
     * равным ID пользователя, которому принадлежит данная сессия.
     * <p>
     * Новый пользователь регистрируется не как модератор и не может менять настройки блога.
     * <p>
     * В параметрах объекта user выводятся имя пользователя, ссылка на его аватар, e-mail,
     * параметры moderation (если равен true, то у пользователя есть права на модерацию и в выпадающем меню
     * справа будет отображаться пункт меню Модерация с цифрой, указанной в параметре moderationCount) и
     * settings (если равен true, то пользователю доступны настройки блога).
     * Оба параметра - moderation и settings - должны быть равны true, если пользователь является модератором.
     * <p>
     * Значение moderationCount содержит количество постов необходимых для проверки модераторами.
     * Считаются посты имеющие статус NEW и не проверерны модератором. Если пользователь не модератор возращать 0 в moderationCount.
     *
     * @param loginRequest - e-mail и пароль пользователя
     * @return - ResultResponse
     */
    public ResultResponse logIn(LoginRequest loginRequest) {
        Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();

        User currentUser = getUserByEmail(loginRequest.getEmail());

        return new UserResultResponse(true, migrateToUserFullResponse(currentUser));
    }

    /**
     * Метод преобразования User в UserFullResponse
     *
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
        SecurityContextHolder.clearContext();

        return new ResultResponse(true);
    }

    /**
     * Метод проверяет наличие в базе пользователя с указанным e-mail.
     * Если пользователь найден, ему должно отправляться письмо со ссылкой на восстановление пароля следующего вида - /login/change-password/HASH,
     * где HASH - сгенерированный код вида b55ca6ea6cb103c6384cfa366b7ce0bdcac092be26bc0
     * (код должен генерироваться случайным образом и сохраняться в базе данных в поле users.code).
     *
     * @param emailRequest - e-mail, на который отправится информация о восстановлении пароля
     */
    public ResultResponse restorePassword(EmailRequest emailRequest) throws MessagingException {
        User user = getUserByEmail(emailRequest.getEmail());
        String hash = UUID.randomUUID().toString();
        String link = rootDomain + changePasswordSubAddress + hash;
        String subject = "Код активации аккаунта ITBlogger";
        String message = "Для восстановления пароля перейдите по <a href=" + link + ">ссылке</a>";

        user.setCode(hash);
        userRepository.save(user);
        mailService.send(emailRequest.getEmail(), subject, message);

        return new ResultResponse(true);
    }

    /**
     * Метод проверяет корректность кода восстановления пароля (параметр code) и корректность кодов капчи:
     * введённый код (параметр captcha) должен совпадать со значением в поле code таблицы captcha_codes,
     * соответствующем пришедшему значению секретного кода (параметр captcha_secret и поле secret_code в таблице базы данных).
     * <p>
     * code - код восстановления пароля
     * password - новый пароль
     * captcha - код капчи
     * captcha_secret - секретный код капчи
     */
    public ResultResponse changePassword(AuthorizationRequest authorizationRequest) {
        User user = getUserByRecoveryCode(authorizationRequest.getCode());

        captchaService.checkCaptcha(authorizationRequest.getCaptcha(), authorizationRequest.getCaptchaSecret());
        textService.checkPasswordLength(authorizationRequest.getPassword());
        user.setPassword(encodePassword(authorizationRequest.getPassword()));
        user.setCode(null);
        userRepository.save(user);

        return new ResultResponse(true);
    }

    /**
     * Метод обрабатывает информацию, введённую пользователем в форму редактирования своего профиля.
     * Если пароль не введён, его не нужно изменять. Если введён, должна проверяться его корректность: достаточная длина.
     * Одинаковость паролей, введённых в двух полях, проверяется на frontend - на сервере проверка не требуется.
     * <p>
     * Запрос без изменения пароля и фотографии: Content-Type: application/json
     * <p>
     * {
     * "name":"Sendel",
     * "email":"sndl@mail.ru"
     * }
     * Запрос c изменением пароля и без изменения фотографии: Content-Type: application/json
     * <p>
     * {
     * "name":"Sendel",
     * "email":"sndl@mail.ru",
     * "password":"123456"
     * }
     * Запрос c изменением пароля и фотографии: Content-Type: multipart/form-data;
     * <p>
     * {
     * "photo": <binary_file>,
     * "name":"Sendel",
     * "email":"sndl@mail.ru",
     * "password":"123456",
     * "removePhoto":0
     * }
     * ⚠️ при загрузке файла изображения фотографии пользователя, необходимо выполнять обрезку и изменение размера фотографии до 36х36 пикселей.
     * <p>
     * Запрос на удаление фотографии без изменения пароля: Content-Type: application/json
     * <p>
     * {
     * "name":"Sendel",
     * "email":"sndl@mail.ru",
     * "removePhoto":1,
     * "photo": ""
     * }
     * <p>
     * photo - файл с фото или пустое значение (если его требуется удалить)
     * removePhoto - параметр, который указывает на то, что фотографию нужно удалить (если значение равно 1)
     * name - новое имя
     * email - новый e-mail
     * password - новый пароль
     *
     * @param image          - загружаемое изображение
     * @param profileRequest - запрос с фронта с данными о профиле
     * @return ResultResponse true, если все успешно или false, в случае ошибки с ее описанием
     */
    public ResultResponse editMyProfile(MultipartFile image, ProfileRequest profileRequest) throws IOException {
        User user = getLoggedUser();
        String avatarPath = imageService.resizeAndWriteImage(user.getId(), image);

        user.setPhoto(avatarPath);
        userRepository.save(user);

        return editMyProfile(profileRequest);
    }

    /**
     * Метод обрабатывает информацию, введённую пользователем в форму редактирования своего профиля.
     * Если пароль не введён, его не нужно изменять. Если введён, должна проверяться его корректность: достаточная длина.
     * Одинаковость паролей, введённых в двух полях, проверяется на frontend - на сервере проверка не требуется.
     * <p>
     * Запрос без изменения пароля и фотографии: Content-Type: application/json
     * <p>
     * {
     * "name":"Sendel",
     * "email":"sndl@mail.ru"
     * }
     * Запрос c изменением пароля и без изменения фотографии: Content-Type: application/json
     * <p>
     * {
     * "name":"Sendel",
     * "email":"sndl@mail.ru",
     * "password":"123456"
     * }
     * <p>
     * Запрос на удаление фотографии без изменения пароля: Content-Type: application/json
     * <p>
     * {
     * "name":"Sendel",
     * "email":"sndl@mail.ru",
     * "removePhoto":1,
     * "photo": ""
     * }
     * <p>
     * removePhoto - параметр, который указывает на то, что фотографию нужно удалить (если значение равно 1)
     * name - новое имя
     * email - новый e-mail
     * password - новый пароль
     *
     * @param profileRequest - запрос с фронта с данными о профиле
     * @return ResultResponse true, если все успешно или false, в случае ошибки с ее описанием
     */
    public ResultResponse editMyProfile(ProfileRequest profileRequest) {
        User user = getLoggedUser();
        String name = profileRequest.getName();
        String email = profileRequest.getEmail();
        String password = profileRequest.getPassword();
        int removePhoto = profileRequest.getRemovePhoto();

        // проверка имени
        textService.checkNameForCorrect(name);
        user.setName(name);
        // проверка e-mail
        textService.checkEmailForCorrect(email);
        if (userRepository.existsByIdAndEmailIgnoreCase(user.getId(), email) || !userRepository.existsByEmailIgnoreCase(email)) {
            user.setEmail(email);
        } else {
            throw new InvalidParameterException("email", "E-mail занят другим пользователем");
        }
        // проверка пароля
        if (password != null) {
            textService.checkPasswordLength(password);
            user.setPassword(encodePassword(password));
        }
        // изменение аватара
        if (removePhoto == photoDeleteValue) {
            imageService.removePhoto(user.getPhoto());
            user.setPhoto("");
        }

        userRepository.save(user);
        return new ResultResponse(true);
    }
}
