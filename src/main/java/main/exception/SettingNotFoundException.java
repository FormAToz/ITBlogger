package main.exception;

public class SettingNotFoundException extends Exception {
    public SettingNotFoundException(String message) {
        super(message);
    }
}
