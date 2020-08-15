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
    private final Map errors;

    public ErrorResultResponse(boolean result, Map errors) {
        this.result = result;
        this.errors = errors;
    }
}
