package de.intelligence.bachelorarbeit.simplifx.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javafx.stage.StageStyle;

/**
 * Automatically configures the {@link javafx.stage.Stage} of the {@link javafx.application.Application}
 * or the {@link javafx.application.Preloader}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StageConfig {

    /**
     * Retrieves the wanted title of the {@link javafx.stage.Stage}.
     *
     * @return The wanted title of the {@link javafx.stage.Stage}.
     */
    String title() default "";

    /**
     * Retrieves the wanted {@link StageStyle} of the {@link javafx.stage.Stage}.
     *
     * @return The wanted {@link StageStyle} of the {@link javafx.stage.Stage}.
     */
    StageStyle style() default StageStyle.DECORATED;

    /**
     * Checks if the {@link javafx.stage.Stage} should be shown always on top.
     *
     * @return If the {@link javafx.stage.Stage} should be shown always on top.
     */
    boolean alwaysTop() default false;

    /**
     * Retrieves the icon paths which will be used as {@link javafx.stage.Stage} icons.
     *
     * @return The icon paths which will be used as {@link javafx.stage.Stage} icons.
     */
    String[] icons() default "";

    /**
     * Checks if the {@link javafx.stage.Stage} should be resizeable.
     *
     * @return If the {@link javafx.stage.Stage} should be resizeable.
     */
    boolean resizeable() default false;

    /**
     * Checks if the {@link javafx.stage.Stage} should automatically be shown after the
     * {@link javafx.application.Application#start} Method was called.
     *
     * @return If the {@link javafx.stage.Stage} should automatically be shown after the
     * {@link javafx.application.Application#start} Method was called.
     */
    boolean autoShow() default false;

}
