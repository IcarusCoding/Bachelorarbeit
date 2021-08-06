package de.intelligence.bachelorarbeit.simplifx.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as config injectable. The field will be injected at instance construction.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {

    /**
     * Retrieves the configuration key.
     *
     * @return The configuration key.
     */
    String value();

    /**
     * Retrieves the default value if no configuration value was found by the specified key.
     *
     * @return The default value if no configuration value was found by the specified key.
     */
    String defaultValue() default "";

}
