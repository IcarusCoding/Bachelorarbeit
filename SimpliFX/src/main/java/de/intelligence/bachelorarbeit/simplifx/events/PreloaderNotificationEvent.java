package de.intelligence.bachelorarbeit.simplifx.events;

import javafx.application.Application;
import javafx.application.Preloader;

public final class PreloaderNotificationEvent extends AbstractApplicationEvent {

    private final Preloader.PreloaderNotification notification;

    public PreloaderNotificationEvent(Preloader.PreloaderNotification notification, Application.Parameters parameters) {
        super(parameters);
        this.notification = notification;
    }

    public Preloader.PreloaderNotification getNotification() {
        return this.notification;
    }

}