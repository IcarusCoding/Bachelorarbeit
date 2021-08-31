package de.intelligence.bachelorarbeit.simplifx.controller.provider;

import javafx.util.Callback;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

/**
 * A {@link IControllerFactoryProvider} which instantiates a controller by its default constructor.
 */
public final class FXMLControllerFactoryProvider implements IControllerFactoryProvider {

    @Override
    public Callback<Class<?>, Object> provide() {
        return this::create;
    }

    @Override
    public Object create(Class<?> clazz) {
        return Reflection.reflect(clazz).findConstructor().instantiate().getReflectable();
    }

}
