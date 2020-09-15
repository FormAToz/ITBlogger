package main.exception;

/**
 * Класс обработки ошибок при работе с пользователями
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
