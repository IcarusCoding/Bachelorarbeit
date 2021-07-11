package de.intelligence.bachelorarbeit.simplifx.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.Module;

import de.intelligence.bachelorarbeit.simplifx.di.DIAnnotation;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DIAnnotation(GuiceEnvironmentFactory.class)
public @interface GuiceInjection {

    Class<? extends Module>[] value();

}
