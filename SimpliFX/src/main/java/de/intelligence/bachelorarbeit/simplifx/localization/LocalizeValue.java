package de.intelligence.bachelorarbeit.simplifx.localization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks fields as external localized. The dynamic localization will be dependent on updatable instance attributes.
 * Can only be used on JavaFX {@link javafx.beans.property.Property} fields.
 *
 * @author Deniz Groenhoff
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalizeValue {

    /**
     * Retrieves the FX-ID of the JavaFX {@link javafx.scene.Node} which will be localized.
     *
     * @return The FX-ID of the JavaFX {@link javafx.scene.Node} which will be localized.
     */
    String id();

    /**
     * Retrieves The parameter index of the translation key.
     *
     * @return The parameter index of the translation key.
     */
    int index() default 0;

    /**
     * Retrieves the property of the {@link javafx.scene.Node} to which a binding should be established.
     *
     * @return The property of the {@link javafx.scene.Node} to which a binding should be established
     */
    String property();

}
