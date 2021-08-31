package de.intelligence.bachelorarbeit.simplifx.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.intelligence.bachelorarbeit.simplifx.di.DIAnnotation;

/**
 * Used to enable dependency injection with the spring framework.
 * This annotation must be only applied to the application entrypoint.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DIAnnotation(SpringEnvironmentFactory.class)
public @interface SpringInjection {

    /**
     * Returns an array of classes which represent spring configuration modules
     *
     * @return An array of classes which represent spring configuration modules
     */
    Class<?>[] value();

}
