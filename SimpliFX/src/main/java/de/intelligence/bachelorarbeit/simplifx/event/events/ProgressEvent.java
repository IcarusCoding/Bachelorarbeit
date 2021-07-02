package de.intelligence.bachelorarbeit.simplifx.event.events;

import javafx.application.Application;

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
