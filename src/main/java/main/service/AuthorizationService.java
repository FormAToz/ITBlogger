package main.service;

import main.api.response.result.ResultResponse;
import main.api.response.user.UserFullResponse;
import main.api.response.result.UserResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuthorizationService {
    @Autowired
    private UserService userService;

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

    public ResponseEntity<ResultResponse> logIn(String e_mail, String password, HttpServletRequest request) {
        if (true) {
            UserFullResponse userFullResponse = new UserFullResponse();
            // TODO реализовать в случае успешной авторизации
            System.out.println(e_mail);
            System.out.println(password);

            return new ResponseEntity<>(new UserResultResponse(true, userFullResponse), HttpStatus.OK);
        }
        else {
            // TODO в случае ошибки
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.UNAUTHORIZED);
        }
    }
}
