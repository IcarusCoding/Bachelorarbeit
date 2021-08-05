package de.intelligence.bachelorarbeit.simplifx.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as an entry point for a JavaFX based {@link javafx.application.Application}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationEntryPoint {

    /**
     * Retrieves the class for the main controller.
     *
     * @return The class for the main controller.
     */
    Class<?> value();

}
