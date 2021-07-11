package de.intelligence.bachelorarbeit.simplifx.exception;

public final class SimpliFXException extends RuntimeException {

    public SimpliFXException() {
        super();
    }

    public SimpliFXException(String message) {
        super(message);
    }

    public SimpliFXException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
