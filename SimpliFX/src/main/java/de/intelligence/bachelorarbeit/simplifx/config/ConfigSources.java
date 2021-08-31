package de.intelligence.bachelorarbeit.simplifx.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An {@link java.lang.annotation.Annotation} to support repeatable {@link ConfigSource} annotations.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigSources {

    ConfigSource[] value();

}
