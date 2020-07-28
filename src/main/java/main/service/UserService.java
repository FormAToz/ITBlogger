package main.service;

import main.Main;
import main.model.Post;
import main.model.User;
import main.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Создание тестового юзера
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
        User user = userRepository.findById(id).get();
        if (user == null) {
            LOGGER.info(MARKER, "Пользователь с id={} не найден", id);
            return null;
        }
        return user;
    }

    public void saveUserIdFromSession(int userId, String sessionId) {
        userIdFromSession = new HashMap<>();
        userIdFromSession.put(sessionId, userId);
    }

    public User getUserFromSession(String sessionId) {
        Integer userId = userIdFromSession.get(sessionId);
        if (userId == null) {
            LOGGER.info(MARKER, "Пользователь не зарегестрирован в сессии {}", sessionId);
            return null;
        }
        return getUserById(userId);
    }

    public boolean isModerator(User user) {
        return user.getIsModerator() == 1;
    }

    public boolean isAuthor(User user, Post post) {
        return user.getId() == post.getUser().getId();
    }
}
