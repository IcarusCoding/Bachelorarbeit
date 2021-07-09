package de.intelligence.bachelorarbeit.simplifx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javafx.css.StyleConverter;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(StyleProperty.class)
public @interface CssProperty {

    String property();

    String localPropertyField() default "";

    Class<? extends StyleConverter<?, ?>> converterClass();

}
