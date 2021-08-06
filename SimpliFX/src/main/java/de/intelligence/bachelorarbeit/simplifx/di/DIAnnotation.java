package de.intelligence.bachelorarbeit.simplifx.di;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A meta annotation which marks an annotation as a configuration annotation for a dependency injection library.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DIAnnotation {

    /**
     * Retrieves the {@link IDIEnvironmentFactory} class connected to the created {@link Annotation}.
     *
     * @return The {@link IDIEnvironmentFactory} class connected to the created {@link Annotation}.
     */
    Class<? extends IDIEnvironmentFactory<? extends Annotation>> value();

}
