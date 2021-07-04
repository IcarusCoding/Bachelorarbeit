package de.intelligence.bachelorarbeit.simplifx.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.intelligence.bachelorarbeit.simplifx.di.IDIEnvironmentFactory;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DIAnnotation {

    Class<? extends IDIEnvironmentFactory<? extends Annotation>> value();

}
