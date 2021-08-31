package de.intelligence.bachelorarbeit.simplifx.dagger1;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.intelligence.bachelorarbeit.simplifx.di.DIAnnotation;

/**
 * Used to enable dependency injection with the dagger1 framework.
 * This annotation must be only applied to the application entrypoint.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DIAnnotation(Dagger1EnvironmentFactory.class)
public @interface Dagger1Injection {

    /**
     * Returns an array of classes which represent dagger1 configuration modules
     *
     * @return An array of classes which represent dagger1 configuration modules
     */
    Class<?>[] value();

}
