package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.beans.binding.StringBinding;

/**
 * An interface that will handle sent notifications in controller groups.
 */
public interface INotificationDialog {

    /**
     * Handles a message that originated from a controller group.
     *
     * @param title   The title of the message.
     * @param content The content of the message.
     * @param kind    The {@link NotificationKind} of the message.
     */
    void handleMessage(StringBinding title, StringBinding content, NotificationKind kind);

    /**
     * Resets the dialog.
     */
    default void reset() {
    }

}
