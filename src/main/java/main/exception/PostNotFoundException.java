package main.exception;

/**
 * Класс обработки ошибок при работе с постами
 */
public class PostNotFoundException extends Exception {
    public PostNotFoundException(String message) {
        super(message);
    }
}
