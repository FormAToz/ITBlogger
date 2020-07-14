package main.api.response;

import java.util.Map;

/**
 * Формат успешного ответа:
 *
 * {
 * 	"result": true
 * 	"errors": null
 * }
 *
 * Формат ответа в случае ошибки или нескольких ошибок:
 *
 * {
 *   "result": false,
 *   "errors": {
 *     "title": "Заголовок не установлен",
 *     "text": "Текст публикации слишком короткий"
 *   }
 * }
 */
public class ResultResponse {
    private final boolean result;
    private final Map errors;

    public ResultResponse(boolean result, Map errors) {
        this.result = result;
        this.errors = errors;
    }

    public boolean isResult() {
        return result;
    }

    public Map getErrors() {
        return errors;
    }
}
