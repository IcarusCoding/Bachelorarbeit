package de.intelligence.bachelorarbeit.simplifx.exception;

/**
 * A {@link ConstructionException} which handles invalid configuration values.
 */
public final class InvalidConfigValueTypeException extends ConstructionException {

    public InvalidConfigValueTypeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public InvalidConfigValueTypeException(String message) {
        super(message);
    }

}
