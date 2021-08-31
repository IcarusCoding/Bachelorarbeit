package de.intelligence.bachelorarbeit.simplifx.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method annotated with this annotation will be invoked when a controller switches into the destruction state.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnDestroy {

    /**
     * Retrieves the invocation order priority.
     * The higher the value, the higher the priority.
     *
     * @return The invocation order priority.
     */
    int value() default 0;

}
