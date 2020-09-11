package main.service;

import main.exception.NoSuchTextLengthException;
import org.springframework.stereotype.Service;

@Service
public class TextService {
    private final int MIN_TITLE_LENGTH = 3;
    private final int MIN_TEXT_LENGTH = 50;
    private final int MIN_ANNOUNCE_TEXT_LENGTH = 40;
    private final int MIN_COMMENT_LENGTH = 3;
    private final int MIN_PASSWORD_LENGTH = 6;

    public void checkTitleAndTextLength(String title, String text) throws NoSuchTextLengthException {
        if (title.length() < MIN_TITLE_LENGTH) {
            throw new NoSuchTextLengthException("title", "Заголовок публикации менее " + MIN_TITLE_LENGTH + " символов");
        }
        if (text.length() < MIN_TEXT_LENGTH) {
            throw new NoSuchTextLengthException("text", "Текст публикации менее " + MIN_TEXT_LENGTH + " символов");
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

    public void checkTextCommentLength(String text) throws NoSuchTextLengthException {
        if (text.length() < MIN_COMMENT_LENGTH) {
            throw new NoSuchTextLengthException("text", "Текст комментария не задан или менее " + MIN_COMMENT_LENGTH + " символов");
        }
    }

    public void checkPasswordLength(String password) throws NoSuchTextLengthException {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new NoSuchTextLengthException("password", "Пароль короче " + MIN_PASSWORD_LENGTH + " символов");
        }
    }
}