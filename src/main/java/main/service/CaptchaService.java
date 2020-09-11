package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.api.response.CaptchaResponse;
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
//        deleteAllExpiredCaptcha();

        Cage cage = new GCage();
        String captcha = cage.getTokenGenerator().next();
        BufferedImage scaledImage = resizeImage(cage.drawImage(captcha), height, width);

        StringBuilder sb = new StringBuilder(imageUrl);
        sb.append(", ")
                .append(Base64.getEncoder().encodeToString(imageToBytes(scaledImage)));

        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setTime(timeService.getExpectedTime(LocalDateTime.now()));
        captchaCode.setCode(captcha);
        // TODO сделать генерацию кода и проверку капчи
        captchaCode.setSecretCode(captcha);
        captchaCodeRepository.save(captchaCode);

        // FIXME проблема со временем(-+  3 часа)
        System.out.println("Code^ " + captchaCodeRepository.findById(1).get().getTime());
        return new CaptchaResponse(captcha, sb.toString());
    }

    /**
     * Метод изменения размера изображения
     */
    private BufferedImage resizeImage(BufferedImage image, int newHeight, int newWidth) {
        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Image resultingImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);

        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

        return outputImage;
    }

    /**
     * Метод преобразования изображения в массив байтов
     */
    private byte[] imageToBytes(BufferedImage image) {
        byte[] bytes = new byte[0];

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            baos.flush();
            bytes = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    /**
     * Метод удаления всех кодов с истекшим сроком действия
     */
    private void deleteAllExpiredCaptcha() {
        List<CaptchaCode> codeList = captchaCodeRepository.findAllExpiredCodes(timeService.getExpiredTimeFromNow());
        System.out.println("Expired^ " + timeService.getExpiredTimeFromNow());
        if (!codeList.isEmpty()) {
            captchaCodeRepository.deleteAll(codeList);
        }
    }
}
