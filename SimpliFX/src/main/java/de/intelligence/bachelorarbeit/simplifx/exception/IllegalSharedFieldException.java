package de.intelligence.bachelorarbeit.simplifx.exception;

import java.lang.reflect.Field;

/**
 * A {@link ConstructionException} which handles invalid shared fields.
 */
public final class IllegalSharedFieldException extends ConstructionException {

    public IllegalSharedFieldException(Field field, String message) {
        super("Field " + field.getDeclaringClass().getName() + "." + field.getName() + " is invalid: " + message);
    }

}
