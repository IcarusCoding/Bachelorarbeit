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

    Class<? extends IDIEnvironmentFactory<? extends Annotation>> value();

}
