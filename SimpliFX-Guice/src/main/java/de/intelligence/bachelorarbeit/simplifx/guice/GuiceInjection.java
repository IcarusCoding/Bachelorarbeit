package de.intelligence.bachelorarbeit.simplifx.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.Module;

import de.intelligence.bachelorarbeit.simplifx.di.DIAnnotation;

/**
 * Used to enable dependency injection with the guice framework.
 * This annotation must be only applied to the application entrypoint.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DIAnnotation(GuiceEnvironmentFactory.class)
public @interface GuiceInjection {

    /**
     * Returns an array of classes which are extending from the guice module class
     *
     * @return An array of classes which are extending from the guice module class
     */
    Class<? extends Module>[] value();

}
