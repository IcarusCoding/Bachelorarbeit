package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.beans.binding.StringBinding;

public interface INotificationDialog {

    void showMessage(StringBinding title, StringBinding content, NotificationKind kind);

    void close();

}
