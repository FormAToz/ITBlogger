package dataBase;

import main.model.User;
import main.repository.Tag2PostRepository;
import main.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataBaseUnitTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    Tag2PostRepository tag2PostRepository;

    @Test
    public void testCreateModerator() {
        User u = new User();
        u.setName("Андрей данилов");
        u.setEmail("7.danilov@gmail.com");
        u.setRegTime(LocalDateTime.now().plusHours(3));
        u.setPhoto("a/b/c.jpeg");
        u.setPassword("123");
        u.setIsModerator(1);
        userRepository.save(u);
    }

    @Test
    public void testTagCountsList() {
        List<?> tagCount = tag2PostRepository.allTagsCount();
        tagCount.forEach(System.out::println);
    }
}
