package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;

public interface IControllerGroup {

    IController getOrConstructController(Class<?> clazz);

    Pane start();

    void createSubGroup(Class<?> originController, Class<?> startController, String groupId, Consumer<Pane> readyConsumer);

    void switchController(Class<?> newController);

    void switchController(Class<?> newController, IWrapperAnimation factory);

    //TODO remove group when last controller destroyed and remove root
    void destroy(Class<?> clazz);

    //TODO unbind everything
    void destroy();

    ControllerGroupContext getContextFor(String groupId);

    Class<?> getActiveController();

    ObjectProperty<VisibilityState> visibilityProperty();

}
