package de.intelligence.bachelorarbeit.simplifx.exception;

/**
 * A {@link ConstructionException} which handles errors while constructing the application.
 */
public final class ApplicationConstructionException extends ConstructionException {

    public ApplicationConstructionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
