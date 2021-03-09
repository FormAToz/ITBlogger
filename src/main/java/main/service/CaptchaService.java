package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.api.response.CaptchaResponse;
import main.exception.InvalidParameterException;
import main.model.CaptchaCode;
import main.repository.CaptchaCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class CaptchaService {

    @Value("${captcha.image.url}")
    private String imageUrl;

    @Value("${captcha.image.height}")
    private int height;

    @Value("${captcha.image.width}")
    private int width;

    @Value("${captcha.image.format}")
    private String format;

    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;

    @Autowired
    private TimeService timeService;

    @Autowired
    private ImageService imageService;

    /**
     * Метод генерирует коды капчи, - отображаемый и секретный, - сохраняет их в базу данных (таблица captcha_codes)
     * и возвращает секретный код secret (поле в базе данных secret_code) и изображение размером 100х35
     * с отображённым на ней основным кодом капчи image (поле базе данных code).
     *
     * Также метод должен удалять устаревшие капчи из таблицы. Время устаревания должно быть задано в конфигурации приложения (по умолчанию, 1 час).
     *
     * Уточнение работы каптчи:
     *
     * При GET запросе:
     * бэк генерирует изображение image и без сохранения на диск конвертит в строку base64, обязательно добавляя к результату заголовок data:image/png;base64,
     * генерирует уникальный идентификатор secret и сохраняет его в бд. По этому идентификатору в дальнейшем будет возможность найти в бд правильный текст каптчи.
     * При восстановлении пароля, регистрации и прочих запросов с captcha, после того как пользователя вводит данные каптчи,
     * отправляется форма содержащая текст-расшифровка каптчи пользователем и secret.
     * Сервис ищем по secret запись о каптче и сравнивает ввод пользователя с code полем записи таблицы captcha_codes.
     * На основе сравнения решается - каптча введена верно или нет.
     */
    public CaptchaResponse generateCaptcha() {
        // Удаляем устаревшие капчи
        deleteAllExpiredCaptcha();

        Cage cage = new GCage();
        String captcha = cage.getTokenGenerator().next();
        String captchaSecret = Base64.getEncoder().encodeToString(captcha.getBytes());
        BufferedImage scaledImage = imageService.resizeImage(cage.drawImage(captcha), height, width);

        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setTime(LocalDateTime.now());
        captchaCode.setCode(captcha);
        captchaCode.setSecretCode(captchaSecret);
        captchaCodeRepository.save(captchaCode);

        String captchaImageUrl = imageUrl + ", " +
                Base64.getEncoder().encodeToString(imageService.imageToBytes(scaledImage, format));
        return new CaptchaResponse(captchaSecret, captchaImageUrl);
    }

    /**
     * Метод удаления всех кодов с истекшим сроком действия
     */
    private void deleteAllExpiredCaptcha() {
        captchaCodeRepository.deleteByTime(timeService.getNowMinusCaptchaExpirationTime());
    }

    /**
     * Метод проверки кода капчи
     *
     * @param captcha - код капчи, что пользователь ввел на фронте
     */
    public void checkCaptcha(String captcha, String captchaSecret) {
        CaptchaCode captchaCode = captchaCodeRepository.findBySecretCode(captchaSecret)
                .orElseThrow(() -> new InvalidParameterException("captcha", "Срок действия секретного кода истек"));

        if (!captchaCode.getCode().equals(captcha)) {
            throw new InvalidParameterException("captcha", "Код с картинки введён неверно");
        }
    }
}
