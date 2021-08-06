package de.intelligence.bachelorarbeit.simplifx.controller.provider;

import javafx.util.Callback;

/**
 * An interface which provides a controller factory for the {@link de.intelligence.bachelorarbeit.simplifx.fxml.SimpliFXMLLoader}.
 */
public interface IControllerFactoryProvider {

    /**
     * Returns a {@link Callback} which instantiates a class.
     *
     * @return A {@link Callback} which instantiates a class.
     */
    Callback<Class<?>, Object> provide();

    Object create(Class<?> clazz);

}
