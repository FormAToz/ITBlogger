package main.service;

import main.Main;
import main.api.response.StatisticsResponse;
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

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public User getUserById(int id) {
        Optional user = userRepository.findById(id);
        if (!user.isPresent()) {
            LOGGER.info(MARKER, "Пользователь с id={} не найден", id);
            return null;
        }
        return (User) user.get();
    }

    public void saveUserIdFromSession(int userId, String sessionId) {
        userIdFromSession = new HashMap<>();
        userIdFromSession.put(sessionId, userId);
    }

    public User getUserFromSession() {
        String sessionId = servletRequest.getSession().getId();
        Integer userId = userIdFromSession.get(sessionId);
        if (userId == null) {
            LOGGER.info(MARKER, "Пользователь не зарегестрирован в сессии {}", sessionId);
            return null;
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
