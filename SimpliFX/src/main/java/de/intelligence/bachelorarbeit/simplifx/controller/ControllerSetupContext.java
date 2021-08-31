package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.binding.StringBinding;
import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;

/**
 * Provides methods for controller group operations that can only be used in the setup phase of a controller.
 */
public final class ControllerSetupContext implements Destructible {

    private final Class<?> controllerClass;
    private final ControllerGroupContext groupCtx;
    private IControllerGroup group;

    ControllerSetupContext(Class<?> controllerClass, IControllerGroup group, ControllerGroupContext groupCtx) {
        this.controllerClass = controllerClass;
        this.group = group;
        this.groupCtx = groupCtx;
    }

    /**
     * Retrieves the current {@link ControllerGroupContext}.
     *
     * @return The current {@link ControllerGroupContext}.
     */
    public ControllerGroupContext getGroupContext() {
        return this.groupCtx;
    }

    /**
     * Creates a new sub group with a custom {@link INotificationDialog}.
     *
     * @param clazz               The class of the start controller for the new group.
     * @param groupId             The id for the new group.
     * @param readyConsumer       The {@link Consumer} which will be used when the controller group is ready.
     * @param notificationHandler The {@link Function} which converts a {@link Pane} to a {@link INotificationDialog}.
     */
    public void createSubGroup(Class<?> clazz, String groupId, Consumer<Pane> readyConsumer,
                               Function<Pane, INotificationDialog> notificationHandler) {
        if (this.group != null) {
            this.group.createSubGroup(this.controllerClass, clazz, groupId, readyConsumer, notificationHandler);
        }
    }

    /**
     * Creates a new sub group.
     *
     * @param clazz         The class of the start controller for the new group.
     * @param groupId       The id for the new group.
     * @param readyConsumer The {@link Consumer} which will be used when the controller group is ready.
     */
    public void createSubGroup(Class<?> clazz, String groupId, Consumer<Pane> readyConsumer) {
        if (this.group != null) {
            this.group.createSubGroup(this.controllerClass, clazz, groupId, readyConsumer, null);
        }
    }

    /**
     * Preloads the specified controllers.
     *
     * @param clazz The classes of the controllers that should get preloaded.
     */
    public void preloadControllers(Class<?>... clazz) {
        if (this.group != null) {
            Arrays.stream(clazz).forEach(this::preloadController);
        }
    }

    /**
     * Preloads the specified controller.
     *
     * @param clazz The class of the controller that should get preloaded.
     */
    public void preloadController(Class<?> clazz) {
        if (this.group != null) {
            this.group.getOrConstructController(clazz);
        }
    }

    /**
     * Shows a notification in the current controller group.
     *
     * @param title   The title of the notification.
     * @param content The content of the notification.
     * @param kind    The {@link NotificationKind} of the notification.
     */
    public void showNotification(String title, String content, NotificationKind kind) {
        this.groupCtx.showNotification(title, content, kind);
    }

    /**
     * Shows a notification in the current controller group.
     *
     * @param title   The title of the notification as a {@link StringBinding}.
     * @param content The content of the notification as a {@link StringBinding}.
     * @param kind    The {@link NotificationKind} of the notification.
     */
    public void showNotification(StringBinding title, StringBinding content, NotificationKind kind) {
        this.groupCtx.showNotification(title, content, kind);
    }

    /**
     * Switches the active controller with a new one.
     *
     * @param clazz The class of the new controller.
     */
    public void switchController(Class<?> clazz) {
        this.groupCtx.switchController(clazz);
    }

    /**
     * Switches the active controller with a new one.
     * The switch will be animated.
     *
     * @param clazz   The class of the new controller.
     * @param factory The {@link IWrapperAnimation} that will be used for the controller switch.
     */
    public void switchController(Class<?> clazz, IWrapperAnimation factory) {
        this.groupCtx.switchController(clazz, factory);
    }

    /**
     * Retrieves the {@link ControllerGroupContext} for another controller group.
     *
     * @param groupId The id of the controller group.
     * @return The {@link ControllerGroupContext} for another controller group.
     */
    public ControllerGroupContext getContextFor(String groupId) {
        return this.groupCtx.getContextFor(groupId);
    }

    /**
     * Retrieves the clazz of the active controller.
     *
     * @return The clazz of the active controller.
     */
    public Class<?> getActiveController() {
        return this.groupCtx.getActiveController();
    }

    @Override
    public void destroy() {
        this.group = null;
    }

}
