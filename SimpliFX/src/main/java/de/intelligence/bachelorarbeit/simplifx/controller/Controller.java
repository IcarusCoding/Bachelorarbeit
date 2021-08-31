package de.intelligence.bachelorarbeit.simplifx.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a controller.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

    /**
     * Retrieves the path to the FXML file.
     *
     * @return The path to the FXML file.
     */
    String fxml();

    /**
     * Retrieves the path to the CSS file.
     *
     * @return The path to the CSS file.
     */
    String css() default "";

}
