package de.intelligence.bachelorarbeit.simplifx.exception;

/**
 * A {@link ConstructionException} which handles errors while constructing a controller.
 */
public final class ControllerConstructionException extends ConstructionException {

    public ControllerConstructionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
