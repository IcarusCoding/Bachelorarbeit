package de.intelligence.bachelorarbeit.simplifx.controller.provider;

import javafx.util.Callback;

public interface IControllerFactoryProvider {

    Callback<Class<?>, Object> provide();

    Object create(Class<?> clazz);

}
