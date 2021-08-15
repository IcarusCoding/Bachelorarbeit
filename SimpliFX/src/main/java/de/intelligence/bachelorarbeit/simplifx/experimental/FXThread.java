package de.intelligence.bachelorarbeit.simplifx.experimental;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method inside a controller that is annotated with this annotation, will be forcefully executed on the FX application thread.
 * Only methods that are public and not final inside a controller class from which inheritance from another package is possible can be annotated.#
 * <p>
 * This is an experimental feature and therefore not recommended to use. Experimental features are disabled by default and can be enabled by calling
 * {@link de.intelligence.bachelorarbeit.simplifx.SimpliFX#enableExperimentalFeatures()} before application launch.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXThread {
}
