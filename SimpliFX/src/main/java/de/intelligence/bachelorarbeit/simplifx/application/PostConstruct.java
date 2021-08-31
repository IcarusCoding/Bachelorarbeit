package de.intelligence.bachelorarbeit.simplifx.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with {@link PostConstruct} will be called after an instance was successfully constructed.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostConstruct {

    /**
     * Retrieves the invocation order priority.
     * The higher the value, the higher the priority.
     *
     * @return The invocation order priority.
     */
    int value() default 0;

}
