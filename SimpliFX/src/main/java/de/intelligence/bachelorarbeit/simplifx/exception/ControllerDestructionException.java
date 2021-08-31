package de.intelligence.bachelorarbeit.simplifx.exception;

/**
 * A {@link ConstructionException} which handles errors while destroying a controller.
 */
public final class ControllerDestructionException extends RuntimeException {

    public ControllerDestructionException(String message) {
        super(message);
    }

}
