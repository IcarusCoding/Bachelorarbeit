package de.intelligence.bachelorarbeit.simplifx.controller.provider;

import javafx.util.Callback;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;

public final class DIControllerFactoryProvider implements IControllerFactoryProvider {

    private final DIEnvironment environment;

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
        return Reflection.reflect(clazz).findConstructor().instantiate().getReflectable();
    }

}
