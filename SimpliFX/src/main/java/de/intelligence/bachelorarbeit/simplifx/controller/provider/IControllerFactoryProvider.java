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

    /**
     * Creates a new instance from the specified class.
     *
     * @param clazz The class from which should be instantiated.
     * @return The created instance.
     */
    Object create(Class<?> clazz);

    /**
     * Processes the created instance.
     *
     * @param instance The instance which should get processed.
     */
    default void handle(Object instance) {
    }

}
