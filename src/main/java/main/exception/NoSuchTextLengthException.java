package main.exception;

public class NoSuchTextLengthException extends Exception {

    private String type;

    public NoSuchTextLengthException() {
    }

    public NoSuchTextLengthException(String message) {
        super(message);
    }

    public NoSuchTextLengthException(String type, String message) {
        super(message);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
