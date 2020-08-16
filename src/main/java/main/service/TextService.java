package main.service;

import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TextService {
    private final int MIN_TITLE_LENGTH = 3;
    private final int MIN_TEXT_LENGTH = 50;
    private final int MIN_ANNOUNCE_TEXT_LENGTH = 40;
    private final int MIN_COMMENT_LENGTH = 3;

    public ResponseEntity<ResultResponse> checkTitleAndTextLength(String title, String text) {
        Map<String, String> errors = new HashMap<>();

        if (title.length() < MIN_TITLE_LENGTH) {
            errors.put("title", "Заголовок публикации менее " + MIN_TITLE_LENGTH + " символов");
        }
        if (text.length() < MIN_TEXT_LENGTH) {
            errors.put("text", "Текст публикации менее " + MIN_TEXT_LENGTH + " символов");
        }
        return new ResponseEntity<>(new ErrorResultResponse(false, errors), HttpStatus.BAD_REQUEST);
    }

    public boolean titleAndTextIsShort(String title, String text) {
        return title.length() < MIN_TITLE_LENGTH || text.length() < MIN_TEXT_LENGTH;
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

    public boolean textCommentIsShort(String text) {
        return text.length() < MIN_COMMENT_LENGTH;
    }

    /**
     * Ответ с ошибкой в случае, если текст комментария короткий
     */
    public ResponseEntity<ResultResponse> textCommentResponseFalse() {
        Map<String, String> errors = new HashMap<>();

        errors.put("text", "Текст комментария не задан или менее " + MIN_COMMENT_LENGTH + " символов");

        return new ResponseEntity<>(new ErrorResultResponse(false, errors), HttpStatus.BAD_REQUEST);
    }
}