package de.intelligence.bachelorarbeit.simplifx.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an one argument method as an event handler.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    /**
     * Retrieves the invocation order priority.
     * The higher the value, the higher the priority.
     *
     * @return The invocation order priority.
     */
    int priority() default 0;

}
