package de.intelligence.bachelorarbeit.simplifx.shared;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a field as a shared resource.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Shared {

    /**
     * Retrieves the name of the shared resource.
     * The first lowercase part of the field name will be used if no name was specified.
     *
     * @return The name of the shared resource.
     */
    String value() default "";

}
