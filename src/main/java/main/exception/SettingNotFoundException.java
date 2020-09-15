package main.exception;

/**
 * Класс обработки ошибок при работе с настройками
 */
public class SettingNotFoundException extends Exception {
    public SettingNotFoundException(String message) {
        super(message);
    }
}
