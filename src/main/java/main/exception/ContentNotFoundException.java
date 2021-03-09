package main.exception;

/**
 * Класс обработки ошибок приложения.
 * Выбрасывается в случае, если контент не найден
 */
public class ContentNotFoundException extends RuntimeException{
    public ContentNotFoundException(String message) {
        super(message);
    }
}
