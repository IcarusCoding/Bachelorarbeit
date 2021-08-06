package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.beans.binding.StringBinding;
import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;

/**
 * An interface which handles the root element of an {@link IControllerGroup}.
 */
public interface IControllerGroupWrapper extends Destructible {

    /**
     * Switches the current {@link IController}.
     *
     * @param controller       The new {@link IController}.
     * @param animationFactory The {@link IWrapperAnimation} used for the controller switch.
     */
    void switchController(IController controller, IWrapperAnimation animationFactory);

    /**
     * Sets a new {@link IController}.
     *
     * @param controller The new {@link IController}.
     */
    void setController(IController controller);

    /**
     * Retrieves the root {@link Pane}.
     *
     * @return The root {@link Pane}.
     */
    Pane getWrapper();

    /**
     * Shows a notification on the underlying root {@link Pane}.
     *
     * @param title   The title of the notification.
     * @param content The content of the notification.
     * @param kind    The {@link NotificationKind} of the notification.
     */
    void showNotification(StringBinding title, StringBinding content, NotificationKind kind);

}
