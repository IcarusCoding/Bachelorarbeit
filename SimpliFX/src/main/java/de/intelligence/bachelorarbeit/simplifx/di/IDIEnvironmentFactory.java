package de.intelligence.bachelorarbeit.simplifx.di;

import java.lang.annotation.Annotation;

public interface IDIEnvironmentFactory<T extends Annotation> {

    DIEnvironment create(Object root, T t);

}
