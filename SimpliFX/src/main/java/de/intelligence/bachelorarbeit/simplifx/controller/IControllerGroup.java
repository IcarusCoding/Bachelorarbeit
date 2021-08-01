package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;

public interface IControllerGroup extends Destructible {

    IController getOrConstructController(Class<?> clazz);

    Pane start();

    void start(Stage primary);

    void createSubGroup(Class<?> originController, Class<?> startController, String groupId, Consumer<Pane> readyConsumer,
                        Function<Pane, INotificationDialog> notificationHandler);

    void switchController(Class<?> newController);

    void switchController(Class<?> newController, IWrapperAnimation factory);

    void destroy(Class<?> clazz);

    ControllerGroupContext getContextFor(String groupId);

    Class<?> getActiveController();

    ObjectProperty<VisibilityState> visibilityProperty();

    ControllerGroupContext getGroupContext();

    Class<?> getStartControllerClass();

    void showNotification(StringBinding title, StringBinding content, NotificationKind kind);

    String getGroupId();

}
