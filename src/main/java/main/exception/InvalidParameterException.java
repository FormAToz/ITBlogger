package main.exception;

/**
 * Класс обработки ошибок по различному типу
 *
 * type - тип ошибки (абзац, текст, заголовок, E-mail и т.д.)
 * message - сообщение об ошибке
 */
public class InvalidParameterException extends Exception {

    private String type;

    public InvalidParameterException() {
    }

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
