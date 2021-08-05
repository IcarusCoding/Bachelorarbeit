package de.intelligence.bachelorarbeit.simplifx.events;

import javafx.application.Application;

/**
 * An {@link AbstractApplicationEvent} for handling the {@link javafx.application.Preloader#handleProgressNotification} method invocation.
 */
public final class ProgressEvent extends AbstractApplicationEvent {

    private final double progress;

    public ProgressEvent(double progress, Application.Parameters parameters) {
        super(parameters);
        this.progress = progress;
    }

    public double getProgress() {
        return this.progress;
    }

}
