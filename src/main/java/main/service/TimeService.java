package main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class TimeService {

    @Value("${captcha.expiration-time}")
    private long expirationTime;

    public LocalDateTime getExpectedTime(LocalDateTime actualTime) {
        if (actualTime.isBefore(LocalDateTime.now())) {
            actualTime = LocalDateTime.now();
        }
        return actualTime.plusHours(3);
    }

    public String timeToString(LocalDateTime actualTime) {
        return DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm").format(actualTime.minusHours(3));
    }

    public LocalDateTime getExpiredTimeFromNow() {
        long now = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .getEpochSecond();

        return LocalDateTime.ofInstant(Instant.ofEpochSecond(now + expirationTime), ZoneId.systemDefault());
    }
}
