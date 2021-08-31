package de.intelligence.bachelorarbeit.simplifx.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javafx.css.StyleConverter;

/**
 * A {@link java.util.List} field annotated with {@link CssProperty} will automatically receive all created {@link javafx.css.CssMetaData}.
 * <p>
 * Example usage:
 *
 * @CssProperty(property = "-fx-...", localPropertyField = "...", converterClass = ..., bindTo = "...")
 * @CssProperty(property = "-fx-...", localPropertyField = "...", converterClass = ..., bindTo = "...")
 * public static List<CssMetaData<? extends Styleable, ?>> CLASS_CSS_META_DATA;
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(StyleProperty.class)
public @interface CssProperty {

    /**
     * Retrieves the name of the css property.
     *
     * @return The name of the css property.
     */
    String property();

    /**
     * Retrieves the name of a {@link javafx.beans.property.Property} field which should hold the value from the css property.
     *
     * @return The name of a {@link javafx.beans.property.Property} field which should hold the value from the css property.
     */
    String localPropertyField();

    /**
     * Retrieves the {@link Class} of a {@link StyleConverter}.
     *
     * @return The {@link Class} of a {@link StyleConverter}.
     */
    Class<? extends StyleConverter<?, ?>> converterClass();

    /**
     * Retrieves the name of the {@link javafx.beans.property.Property} to which a binding should occur.
     *
     * @return The name of the {@link javafx.beans.property.Property} to which a binding should occur.
     */
    String bindTo() default "";

}
