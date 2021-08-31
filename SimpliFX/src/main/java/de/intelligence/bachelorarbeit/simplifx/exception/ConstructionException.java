package de.intelligence.bachelorarbeit.simplifx.exception;

/**
 * A {@link SimpliFXException} which handles errors that originated in a construction process.
 */
public class ConstructionException extends SimpliFXException {

    public ConstructionException() {
        super();
    }

    public ConstructionException(String message) {
        super(message);
    }

    public ConstructionException(Throwable throwable) {
        super(throwable);
    }

    public ConstructionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
