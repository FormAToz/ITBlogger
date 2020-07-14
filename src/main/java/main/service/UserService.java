package main.service;

import main.model.User;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // Создание тестового юзера
    public void createTestUser() {
        User u = new User();
        u.setName("Андрей данилов");
        u.setEmail("7.danilov@gmail.com");
        u.setRegTime(LocalDateTime.now().plusHours(3));
        u.setPhoto("a/b/c.jpeg");
        u.setPassword("123");
        u.setIsModerator(1);
        userRepository.save(u);
    }

    public User getUserById(int id) {
        // TODO добавить логгер и обработчик, если юзер не найден
        return userRepository.findById(id).get();
    }
}
