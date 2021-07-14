package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;

public interface IControllerGroup {

    IController constructController(Class<?> clazz);

    Pane start(IControllerGroupWrapper wrapper);

    void registerSubGroup(Class<?> originController, Class<?> startController, String groupId, Consumer<Pane> readyConsumer);

    void switchController(Class<?> newController);

    void switchController(Class<?> newController, IWrapperAnimationFactory factory);

    //TODO remove group when last controller destroyed and remove root
    void destroy(Class<?> clazz);

    //TODO unbind everything
    void destroy();

    ControllerGroupContext getContextFor(String groupId);

    Class<?> getActiveController();

    ObjectProperty<VisibilityState> visibilityProperty();

   /* Pane start(IControllerGroupWrapper wrapper);

    void destroy(Class<?> clazz);

    void destroy();

    Class<?> getStartController();

    void registerSubGroup(Class<?> startController, String groupId, Consumer<Pane> readyConsumer);

    void switchController(Class<?> newController, IWrapperAnimationFactory factory);

    void switchController(Class<?> newController);

    ControllerGroupContext getContextFor(String groupId);

    Class<?> getActiveController();*/

}
