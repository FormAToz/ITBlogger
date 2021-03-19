package main.service;

import main.exception.InvalidParameterException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TextService {
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final String NAME_PATTERN = "[A-Za-zА-Яа-я0-9\\s]{1,20}";

    @Value("${text.min-title-length}")
    private int minTitleLength;

    @Value("${text.max-title-length}")
    private int maxTitleLength;

    @Value("${text.min-text-length}")
    private int minTextLength;

    @Value("${text.max-text-length}")
    private int maxTextLength;

    @Value("${text.max-announce-text-length}")
    private int maxAnnounceTextLength;

    @Value("${text.min-comment-length}")
    private int minCommentLength;

    @Value("${text.min-password-length}")
    private int minPasswordLength;

    public void checkTitleAndTextLength(String title, String text) {
        if (title.length() < minTitleLength) {
            throw new InvalidParameterException("title", "Заголовок публикации менее " + minTitleLength + " символов");
        }

        if (title.length() > maxTitleLength) {
            throw new InvalidParameterException("title", "Заголовок публикации более " + maxTitleLength + " символов");
        }

        if (text.length() < minTextLength) {
            throw new InvalidParameterException("text", "Текст публикации менее " + minTextLength + " символов");
        }

        if (text.length() > maxTextLength) {
            throw new InvalidParameterException("text", "Текст публикации более " + maxTextLength + " символов");
        }
    }

    /**
     * Анонс поста (кусок текста) с проверкой кол-ва символов без HTML тэгов
     */
    public String getAnnounce(String text) {
        String postAnnounce = (text.length() > maxAnnounceTextLength)
                ? text.substring(0, maxAnnounceTextLength).concat("...")
                : text;

        return postAnnounce.replaceAll("(<.*?>)|(&.*?;)|([ ]{2,})", " ");
    }

    public void checkTextCommentLength(String text) {
        if (text.length() < minCommentLength) {
            throw new InvalidParameterException("text", "Текст комментария не задан или менее " + minCommentLength + " символов");
        }
    }

    /**
     * Метод проверки длины пароля
     * @param password пароль
     * @throws InvalidParameterException в случае, если длина недостаточна
     */
    public void checkPasswordLength(String password) {
        if (password.length() < minPasswordLength) {
            throw new InvalidParameterException("password", "Пароль короче " + minPasswordLength + " символов");
        }
    }

    /**
     * Метод проверки длины и корректности имени
     *
     * @param name - имя пользователя (без числовых символов)
     */
    public void checkNameForCorrect(String name) {
        if (name == null || name.isEmpty()) {
            throw new InvalidParameterException("name", "Имя пользователя не введено");
        }

        if (!name.matches(NAME_PATTERN)) {
            throw new InvalidParameterException("name", "Имя указано неверно");
        }
    }

    /**
     * Метод проверки длины и корректности e-mail
     *
     * @param email - e-mail пользователя
     */
    public void checkEmailForCorrect(String email) {
        if (email == null || email.isEmpty()) {
            throw new InvalidParameterException("email", "E-mail не введен");
        }

        if (!email.matches(EMAIL_PATTERN)) {
            throw new InvalidParameterException("email", "введите корректный e-mail");
        }
    }
}