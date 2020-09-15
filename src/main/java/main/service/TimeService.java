package main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TimeService {

    @Value("${captcha.expiration-time}")
    private long expirationTime;

    /**
     * Метод преобразует long в LocalDateTime. и если время меньше текущего, то оно становится текущим
     *
     * @param actualTime - timestamp в секундах
     * @return LocalDateTime
     */
    public LocalDateTime getExpectedTime(long actualTime) {
        LocalDateTime postTime = Instant.ofEpochSecond(actualTime).atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (postTime.isBefore(LocalDateTime.now())) {
            postTime = LocalDateTime.now();
        }

        return postTime;
    }

    /**
     * Метод возвращает текущее время в секундах
     * @return long - timestamp в секундах
     */
    public long getTimestampFromNow() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * Метод преобразования LocalDateTime в секунды
     * @param localDateTime - время для преобразования
     * @return - long, время в секундах
     */
    public long getTimestampFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * Метод вычисления времени истечения срока действия капчи.
     *
     * @return LocalDateTime: актуальное время минус срок действия капчи
     */
    public LocalDateTime getNowMinusCaptchaExpirationTime() {
        return LocalDateTime
                .ofInstant(Instant.ofEpochSecond(getTimestampFromNow() - expirationTime), ZoneId.systemDefault());
    }
}
