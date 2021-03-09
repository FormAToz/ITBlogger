package main.api.response.result;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс ответа ошибок по соответствию тип - сообщение
 *
 * Пример формата ответа в случае ошибок:
 *
 * {
 *   "result": false,
 *   "errors": {
 *     "title": "Заголовок не установлен",
 *     "text": "Текст публикации слишком короткий"
 *   }
 * }
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResultResponse extends ResultResponse{
    private Map<String, String> errors = new HashMap<>();

    public ErrorResultResponse(boolean result) {
        super(result);
    }

    public ErrorResultResponse(boolean result, String message) {
        super(result);
        errors.put("message", message);
    }

    public ErrorResultResponse(boolean result, String type, String message) {
        super(result);
        errors.put(type, message);
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
