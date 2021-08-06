package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.Pane;

/**
 * An interface that manages one single controller.
 */
public interface IController extends Destructible {

    /**
     * Retrieves the instance of the controller.
     *
     * @return The instance of the controller.
     */
    Object getControllerInstance();

    /**
     * Retrieves the root {@link Pane} of the controller.
     *
     * @return The root {@link Pane} of the controller.
     */
    Pane getRoot();

    /**
     * Retrieves the class of the controller.
     *
     * @return The class of the controller.
     */
    Class<?> getControllerClass();

    /**
     * Retrieves all sub groups that are registered to the controller.
     *
     * @return A {@link Map} containing all sub groups that are registered to the controller.
     */
    Map<String, IControllerGroup> getSubGroups();

    /**
     * Retrieves the {@link VisibilityState} of the controller as an {@link ObjectProperty}.
     *
     * @return The {@link VisibilityState} of the controller as an {@link ObjectProperty}.
     */
    ObjectProperty<VisibilityState> visibilityProperty();

    /**
     * Retrieves the {@link VisibilityState} of the controller.
     *
     * @return The {@link VisibilityState} of the controller.
     */
    VisibilityContext getVisibilityContext();

    /**
     * Saves a reference to a {@link ChangeListener} to prevent early garbage collection.
     *
     * @param listener A reference to a {@link ChangeListener}.
     */
    void registerWeakListener(ChangeListener<?> listener);

}
