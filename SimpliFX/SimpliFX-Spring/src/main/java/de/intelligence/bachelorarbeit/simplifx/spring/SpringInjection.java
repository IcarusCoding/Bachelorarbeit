package de.intelligence.bachelorarbeit.simplifx.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.intelligence.bachelorarbeit.simplifx.annotation.DIAnnotation;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DIAnnotation(SpringEnvironmentFactory.class)
public @interface SpringInjection {

    Class<?>[] value();

}
