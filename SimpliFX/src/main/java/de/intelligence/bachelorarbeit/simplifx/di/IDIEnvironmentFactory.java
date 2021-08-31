package de.intelligence.bachelorarbeit.simplifx.di;

import java.lang.annotation.Annotation;

/**
 * A factory which creates a {@link DIEnvironment} instance by the specified {@link Annotation}.
 *
 * @param <T> The {@link Annotation} which will be used as a base for the {@link DIEnvironment} configuration.
 */
public interface IDIEnvironmentFactory<T extends Annotation> {

    /**
     * Creates an instance of the {@link DIEnvironment} class.
     *
     * @param t The {@link Annotation} for the configuration of a {@link DIEnvironment} instance.
     * @return A new instance of the {@link DIEnvironment} class.
     */
    DIEnvironment create(T t);

}
