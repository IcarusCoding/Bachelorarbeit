package de.intelligence.bachelorarbeit.simplifx.controller.provider;

import javafx.util.Callback;

import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;

/**
 * A {@link IControllerFactoryProvider} which supports dependency injection.
 */
public final class DIControllerFactoryProvider implements IControllerFactoryProvider {

    private final DIEnvironment environment;

    /**
     * Creates a new {@link DIControllerFactoryProvider} with the specified {@link DIEnvironment}.
     *
     * @param environment The {@link DIEnvironment} to use for dependency injection.
     */
    public DIControllerFactoryProvider(DIEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public Callback<Class<?>, Object> provide() {
        return clazz -> {
            final Object instance = this.create(clazz);
            this.environment.inject(instance);
            return instance;
        };
    }

    @Override
    public Object create(Class<?> clazz) {
        return this.environment.get(clazz);
    }

}
