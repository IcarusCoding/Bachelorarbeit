package de.intelligence.bachelorarbeit.simplifx.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ConfigSources.class)
public @interface ConfigSource { //TODO add support for outside classpath

    String value();

    Source source() default Source.CLASSPATH;

    enum Source {

        CLASSPATH,
        FILESYSTEM

    }

}
