package de.intelligence.bachelorarbeit.simplifx.application;

import javafx.application.Preloader;

public final class PreloaderNotificationAccess {

    private final Preloader preloader;

    public PreloaderNotificationAccess(Preloader preloader) {
        this.preloader = preloader;
    }

    public void notifyPreloader(Preloader.PreloaderNotification notification) {
        this.preloader.handleApplicationNotification(notification);
    }

}
