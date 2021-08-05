package de.intelligence.bachelorarbeit.simplifx.exception;

/**
 * A {@link ConstructionException} which handles invalid configuration files.
 */
public final class InvalidConfigFileException extends ConstructionException {

    public InvalidConfigFileException(String message) {
        super(message);
    }

}
