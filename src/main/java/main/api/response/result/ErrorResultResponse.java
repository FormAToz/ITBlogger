package main.api.response.result;

import java.util.Map;

/**
 * Формат ответа в случае ошибок:
 *
 * {
 *   "result": false,
 *   "errors": {
 *     "title": "Заголовок не установлен",
 *     "text": "Текст публикации слишком короткий"
 *   }
 * }
 */
public class ErrorResultResponse extends ResultResponse{
    private final Map<String, String> errors;

    public ErrorResultResponse(boolean result, Map<String, String> errors) {
        super(result);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
