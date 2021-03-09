package main.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Класс обработки ошибок по различному типу
 *
 * type - тип ошибки (абзац, текст, заголовок, E-mail и т.д.)
 * message - сообщение об ошибке
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvalidParameterException extends RuntimeException {

    private String type;

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(String type, String message) {
        super(message);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
