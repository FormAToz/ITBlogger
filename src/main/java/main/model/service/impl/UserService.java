package main.model.service.impl;

import main.model.entity.User;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // сохранение тестового юзера
//    @PostConstruct
//    private void initUser() {
//        User defaultUser = new User();
//        defaultUser.setIsModerator(1);
//        defaultUser.setEmail("7.danilov@gmail.com");
//        defaultUser.setName("Андрей Данилов");
//        defaultUser.setPassword("123");
//        defaultUser.setPhoto("a/b/c.jpg");
//        defaultUser.setRegTime(LocalDateTime.now());
//        userRepository.save(defaultUser);
//    }

    public User getUserById(int id) {
        return userRepository.findById(id).get();
    }
}
