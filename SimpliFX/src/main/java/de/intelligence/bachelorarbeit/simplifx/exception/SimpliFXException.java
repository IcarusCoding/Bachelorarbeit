package de.intelligence.bachelorarbeit.simplifx.exception;

public class SimpliFXException extends RuntimeException {

    public SimpliFXException() {
        super();
    }

    public SimpliFXException(String message) {
        super(message);
    }

    public SimpliFXException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SimpliFXException(Throwable throwable) {
        super(throwable);
    }

}
