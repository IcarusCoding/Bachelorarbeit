package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.function.Consumer;

import javafx.scene.layout.Pane;

public interface IControllerGroup {

    Pane start(IControllerGroupWrapper wrapper);

    void destroy(Class<?> clazz);

    void destroy();

    Class<?> getStartController();

    void registerSubGroup(Class<?> startController, String groupId, Consumer<Pane> readyConsumer);

    void switchController(Class<?> newController, IWrapperAnimationFactory factory);

    void switchController(Class<?> newController);

    ControllerGroupContext getContextFor(String groupId);

    Class<?> getActiveController();

}
