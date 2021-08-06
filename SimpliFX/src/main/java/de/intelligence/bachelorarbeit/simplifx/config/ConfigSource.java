package de.intelligence.bachelorarbeit.simplifx.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a configuration source.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ConfigSources.class)
public @interface ConfigSource {

    /**
     * Retrieves the path to the properties file.
     *
     * @return The path to the properties file.
     */
    String value();

    /**
     * Retrieves the {@link Source} of the properties file.
     *
     * @return The {@link Source} of the properties file.
     */
    Source source() default Source.CLASSPATH;

    /**
     * An enum to differentiate between classpath and external configuration files.
     */
    enum Source {

        /**
         * The configuration file is located inside the classpath.
         */
        CLASSPATH,
        /**
         * The configuration file is located outside of the classpath.
         */
        FILESYSTEM

    }

}
