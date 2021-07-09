package de.intelligence.bachelorarbeit.simplifx.localization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ResourceBundles.class)
public @interface ResourceBundle {

    String value() default "";

}
