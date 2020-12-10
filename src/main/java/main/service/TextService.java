package main.service;

import main.exception.InvalidParameterException;
import org.springframework.stereotype.Service;

@Service
public class TextService {
    private final int MIN_TITLE_LENGTH = 3;
    private final int MIN_TEXT_LENGTH = 50;
    private final int MIN_ANNOUNCE_TEXT_LENGTH = 40;
    private final int MIN_COMMENT_LENGTH = 3;
    private final int MIN_PASSWORD_LENGTH = 6;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final String NAME_PATTERN = "[\\w\\s&&[^0-9]]+";

    public void checkTitleAndTextLength(String title, String text) throws InvalidParameterException {
        if (title.length() < MIN_TITLE_LENGTH) {
            throw new InvalidParameterException("title", "Заголовок публикации менее " + MIN_TITLE_LENGTH + " символов");
        }
        if (text.length() < MIN_TEXT_LENGTH) {
            throw new InvalidParameterException("text", "Текст публикации менее " + MIN_TEXT_LENGTH + " символов");
        }
    }

    /**
     * Анонс поста (кусок текста) с проверкой кол-ва символов без HTML тэгов
     */
    public String getAnnounce(String text) {
        String postAnnounce = (text.length() > MIN_ANNOUNCE_TEXT_LENGTH)
                ? text.substring(0, MIN_ANNOUNCE_TEXT_LENGTH).concat("...")
                : text;

        return postAnnounce.replaceAll("(<.*?>)|(&.*?;)|([ ]{2,})", " ");
    }

    public void checkTextCommentLength(String text) throws InvalidParameterException {
        if (text.length() < MIN_COMMENT_LENGTH) {
            throw new InvalidParameterException("text", "Текст комментария не задан или менее " + MIN_COMMENT_LENGTH + " символов");
        }
    }

    public void checkPasswordLength(String password) throws InvalidParameterException {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidParameterException("password", "Пароль короче " + MIN_PASSWORD_LENGTH + " символов");
        }
    }

    /**
     * Метод проверки длины и корректности имени
     *
     * @param name - имя пользователя (без числовых символов)
     */
    public void checkNameForCorrect(String name) throws InvalidParameterException {
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
    public void checkEmailForCorrect(String email) throws InvalidParameterException {
        if (email == null || email.isEmpty()) {
            throw new InvalidParameterException("email", "E-mail не введен");
        }

        if (!email.matches(EMAIL_PATTERN)) {
            throw new InvalidParameterException("email", "введите корректный e-mail");
        }
    }
}