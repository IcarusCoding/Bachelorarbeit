package de.intelligence.bachelorarbeit.simplifx.dagger1;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.intelligence.bachelorarbeit.simplifx.annotation.DIAnnotation;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DIAnnotation(Dagger1EnvironmentFactory.class)
public @interface Dagger1Injection {

    Class<?>[] value();

}
