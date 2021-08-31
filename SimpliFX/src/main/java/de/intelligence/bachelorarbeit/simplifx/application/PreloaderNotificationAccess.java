package de.intelligence.bachelorarbeit.simplifx.application;

import javafx.application.Preloader;

/**
 * A class with which a direct method call to the {@link Preloader#notifyPreloader} is possible.
 * The {@link javafx.application.Application#notifyPreloader} or {@link com.sun.javafx.application.LauncherImpl#notifyPreloader} have no effect on
 * the {@link Preloader} managed by SimpliFX.
 */
public final class PreloaderNotificationAccess {

    private final Preloader preloader;

    public PreloaderNotificationAccess(Preloader preloader) {
        this.preloader = preloader;
    }

    public void notifyPreloader(Preloader.PreloaderNotification notification) {
        this.preloader.handleApplicationNotification(notification);
    }

}
