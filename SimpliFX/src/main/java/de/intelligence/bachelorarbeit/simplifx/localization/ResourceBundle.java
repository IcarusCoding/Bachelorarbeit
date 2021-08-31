package de.intelligence.bachelorarbeit.simplifx.localization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to provide paths to new {@link ResourceBundle} instances in the classpath.
 *
 * @author Deniz Groenhoff
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceBundle {

    /**
     * Retrieves the paths to the {@link ResourceBundle} locations in the classpath.
     *
     * @return The paths to the {@link ResourceBundle} locations in the classpath.
     */
    String[] value() default "";

}
