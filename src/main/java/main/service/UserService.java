package main.service;

import main.Main;
import main.api.response.StatisticsResponse;
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
    public boolean notModerator(User user) {
        return user.getIsModerator() == 0;
    }

    public boolean notAuthor(User user, Post post) {
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
}
