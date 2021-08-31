package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;

/**
 * An interface which handles a controller group.
 */
public interface IControllerGroup extends Destructible {

    /**
     * Creates a new controller or retrieves an already existing one from this group.
     *
     * @param clazz The class of the controller.
     * @return A new controller or retrieves an already existing one from this group.
     */
    IController getOrConstructController(Class<?> clazz);

    /**
     * Starts the controller group.
     *
     * @return The root {@link Pane} of the underlying {@link IControllerGroupWrapper}.
     */
    Pane start();

    /**
     * Starts the controller group when the specified {@link Stage} is shown for the first time.
     *
     * @param primary The {@link Stage} which will initiate this controller group.
     */
    void start(Stage primary);

    /**
     * Creates a new sub controller group.
     *
     * @param originController    The clazz of the controller that will be treated as super controller.
     * @param startController     The clazz of the start controller for the new group.
     * @param groupId             The id for the new group.
     * @param readyConsumer       The {@link Consumer} which will be used when the controller group is ready.
     * @param notificationHandler The {@link Function} that converts an {@link Pane} into an {@link INotificationDialog}.
     */
    void createSubGroup(Class<?> originController, Class<?> startController, String groupId, Consumer<Pane> readyConsumer,
                        Function<Pane, INotificationDialog> notificationHandler);

    /**
     * Switches the active controller with a new one.
     *
     * @param newController The class of the new controller.
     */
    void switchController(Class<?> newController);

    /**
     * Switches the active controller with a new one.
     * The switch will be animated.
     *
     * @param newController The class of the new controller.
     * @param factory       The {@link IWrapperAnimation} that will be used for the controller switch.
     */
    void switchController(Class<?> newController, IWrapperAnimation factory);

    /**
     * Destroys and fully unregisters a sub controller by its class.
     *
     * @param clazz The class of the sub controller.
     */
    void destroy(Class<?> clazz);

    /**
     * Retrieves the {@link ControllerGroupContext} for another controller group.
     *
     * @param groupId The id of the controller group.
     * @return The {@link ControllerGroupContext} for another controller group.
     */
    ControllerGroupContext getContextFor(String groupId);

    /**
     * Retrieves the clazz of the active controller.
     *
     * @return The clazz of the active controller.
     */
    Class<?> getActiveController();

    /**
     * Retrieves the {@link VisibilityState} for this controller group as an {@link ObjectProperty}.
     *
     * @return The {@link VisibilityState} for this controller group as an {@link ObjectProperty}.
     */
    ObjectProperty<VisibilityState> visibilityProperty();

    /**
     * Retrieves the {@link ControllerGroupContext} of this controller group.
     *
     * @return The {@link ControllerGroupContext} of this controller group.
     */
    ControllerGroupContext getGroupContext();

    /**
     * Retrieves the clazz of the start controller.
     *
     * @return The clazz of the start controller.
     */
    Class<?> getStartControllerClass();

    /**
     * Shows a notification in this controller group.
     *
     * @param title   The title of the notification.
     * @param content The content of the notification.
     * @param kind    The {@link NotificationKind} of the notification.
     */
    void showNotification(StringBinding title, StringBinding content, NotificationKind kind);

    /**
     * Retrieves the id of this controller group.
     *
     * @return The id of this controller group.
     */
    String getGroupId();

}
