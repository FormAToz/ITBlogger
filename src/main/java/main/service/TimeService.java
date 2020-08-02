package main.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TimeService {
    /**
     * Если время публикации раньше текущего времени, оно должно автоматически становиться текущим.
     * Если позже текущего - необходимо устанавливать введенное значение.
     * */
    public LocalDateTime getExpectedTime(LocalDateTime actualTime) {
        if (actualTime.isBefore(LocalDateTime.now())) {
            actualTime = LocalDateTime.now();
        }
        return actualTime.plusHours(3);
    }

    public String timeToString(LocalDateTime actualTime) {
        return DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm").format(actualTime.minusHours(3));
    }
}
