package de.intelligence.bachelorarbeit.simplifx.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javafx.stage.StageStyle;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StageConfig {

    String title() default "";

    StageStyle style() default StageStyle.DECORATED;

    boolean alwaysTop() default false;

    String icons() default "";

    boolean resizeable() default false;

    boolean autoShow() default false;

}
