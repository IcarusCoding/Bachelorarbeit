package de.intelligence.bachelorarbeit.simplifx.events;

import javafx.application.Application;

import de.intelligence.bachelorarbeit.simplifx.application.PreloaderNotificationAccess;

public final class InitEvent extends AbstractApplicationEvent {

    private final PreloaderNotificationAccess access;

    public InitEvent(PreloaderNotificationAccess access, Application.Parameters parameters) {
        super(parameters);
        this.access = access;
    }

    public PreloaderNotificationAccess getNotificationAccess() {
        return this.access;
    }

}
