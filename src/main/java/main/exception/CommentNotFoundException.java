package main.exception;

/**
 * Класс обработки ошибок при работе с комментариями
 */
public class CommentNotFoundException extends Exception{
    public CommentNotFoundException(String message) {
        super(message);
    }
}
