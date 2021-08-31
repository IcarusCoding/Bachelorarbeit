package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;

import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;

public final class ControllerGroupContext implements Destructible {

    private IControllerGroup group;

    ControllerGroupContext(IControllerGroup group) {
        this.group = group;
    }

    /**
     * Switches the active controller with a new one.
     *
     * @param clazz The class of the new controller.
     */
    public void switchController(Class<?> clazz) {
        if (this.group != null) {
            this.group.switchController(clazz);
        }
    }

    /**
     * Switches the active controller with a new one.
     * The switch will be animated.
     *
     * @param clazz   The class of the new controller.
     * @param factory The {@link IWrapperAnimation} that will be used for the controller switch.
     */
    public void switchController(Class<?> clazz, IWrapperAnimation factory) {
        if (this.group != null) {
            this.group.switchController(clazz, factory);
        }
    }

    /**
     * Retrieves the {@link ControllerGroupContext} for another controller group.
     *
     * @param groupId The id of the controller group.
     * @return The {@link ControllerGroupContext} for another controller group.
     */
    public ControllerGroupContext getContextFor(String groupId) {
        if (this.group != null) {
            return this.group.getContextFor(groupId);
        }
        return null;
    }

    /**
     * Retrieves the clazz of the active controller.
     *
     * @return The clazz of the active controller.
     */
    public Class<?> getActiveController() {
        if (this.group != null) {
            return this.group.getActiveController();
        }
        return null;
    }

    /**
     * Shows a notification in the current controller group.
     *
     * @param title   The title of the notification.
     * @param content The content of the notification.
     * @param kind    The {@link NotificationKind} of the notification.
     */
    public void showNotification(String title, String content, NotificationKind kind) {
        this.showNotification(Bindings.createStringBinding(() -> title), Bindings.createStringBinding(() -> content), kind);
    }

    /**
     * Shows a notification in the current controller group.
     *
     * @param title   The title of the notification as a {@link StringBinding}.
     * @param content The content of the notification as a {@link StringBinding}.
     * @param kind    The {@link NotificationKind} of the notification.
     */
    public void showNotification(StringBinding title, StringBinding content, NotificationKind kind) {
        if (this.group != null) {
            this.group.showNotification(title, content, kind);
        }
    }

    /**
     * Destroys a controller in the controller group.
     *
     * @param clazz The class of the controller that should get destroyed.
     */
    public void destroyController(Class<?> clazz) {
        if (this.group != null) {
            this.group.destroy(clazz);
        }
    }

    /**
     * Destroys the controller group.
     */
    public void destroyGroup() {
        if (this.group != null) {
            this.group.destroy();
        }
    }

    @Override
    public void destroy() {
        this.group = null;
    }

}
