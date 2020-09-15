package main.exception;

/**
 * Класс обработки общих ошибок приложения
 */
public class ApplicationException extends Exception {
    public ApplicationException(String message) {
        super(message);
    }
}
