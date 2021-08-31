package de.intelligence.bachelorarbeit.simplifx.exception;

/**
 * A basic {@link RuntimeException} for handling errors which originated from a SimpliFX context.
 */
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
