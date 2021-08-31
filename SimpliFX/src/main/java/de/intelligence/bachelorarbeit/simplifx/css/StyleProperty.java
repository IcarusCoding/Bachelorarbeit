package de.intelligence.bachelorarbeit.simplifx.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An {@link java.lang.annotation.Annotation} to support repeatable {@link CssProperty} annotations.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StyleProperty {

    CssProperty[] value();

}
