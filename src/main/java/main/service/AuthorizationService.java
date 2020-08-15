package main.service;

import main.api.request.AuthorizationRequest;
import main.api.response.CaptchaResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.api.response.user.UserFullResponse;
import main.api.response.result.UserResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthorizationService {
    @Autowired
    private UserService userService;

    /**
     * Метод создаёт пользователя в базе данных, если введённые данные верны.
     * Если данные неверные - пользователь не создаётся, а метод возвращает соответствующую ошибку.
     *
     * e_mail - e-mail пользователя
     * name - имя пользователя
     * password - пароль для аккаунта
     * captcha - код капчи
     * captcha_secret - секретный код капчи
     */
    public ResponseEntity<ResultResponse> register(AuthorizationRequest authorizationRequest) {
        if (true) {
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }
        else {
            Map<String, String> errors = new HashMap<>();

            return new ResponseEntity<>(new ErrorResultResponse(false, errors), HttpStatus.OK);
        }
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
            UserFullResponse userFullResponse = new UserFullResponse();

            userFullResponse.setName("Андрей Данилов");
            userFullResponse.setId(1);
            userFullResponse.setEmail("7.danilov@gmail.com");
            userFullResponse.setModeration(true);
            userFullResponse.setModerationCount(0);
            userFullResponse.setSettings(true);
            userService.saveUserIdFromSession(1, sessionId);

            return new ResponseEntity<>(new UserResultResponse(true, userFullResponse), HttpStatus.OK);
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
            UserFullResponse userFullResponse = new UserFullResponse();
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

    /**
     * Метод генерирует коды капчи, - отображаемый и секретный, - сохраняет их в базу данных (таблица captcha_codes)
     * и возвращает секретный код secret (поле в базе данныхsecret_code) и изображение размером 100х35
     * с отображённым на ней основным кодом капчи image (поле базе данных code).
     *
     * Также метод должен удалять устаревшие капчи из таблицы. Время устаревания должно быть задано в конфигурации приложения (по умолчанию, 1 час).
     *
     * Уточнение работы каптчи:
     *
     * При запросе GET /api/auth/captcha:
     * бэк генерирует изображение image и без сохранения на диск конвертит в строку base64, обязательно добавляя к результату заголовок data:image/png;base64,
     * генерирует уникальный идентификатор secret и сохраняет его в бд. По этому идентификтору в дальнейшем будет возможность найти в бд правильный текст каптчи.
     * При восстановлении пароля, регистрации и прочих запросов с captcha, после того как пользователя вводит данные каптчи,
     * отправляется форма содержащая текст-расшифровка каптчи пользователем и secret.
     * Сервис ищем по secret запись о каптче и сравнивает ввод пользователя с code полем записи таблицы captcha_codes.
     * На основе сравнения решается - каптча введена верно или нет.
     */
    public ResponseEntity<CaptchaResponse> generateCaptcha() {
        // TODO
        String secret = "";
        String image = "";
        return new ResponseEntity<>(new CaptchaResponse(secret, image), HttpStatus.OK);
    }
}
