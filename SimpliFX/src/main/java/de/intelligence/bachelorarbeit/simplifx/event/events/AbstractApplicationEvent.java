package de.intelligence.bachelorarbeit.simplifx.event.events;

import javafx.application.Application;

public abstract class AbstractApplicationEvent extends AbstractEvent {

    private final Application.Parameters parameters;

    protected AbstractApplicationEvent(Application.Parameters parameters) {
        this.parameters = parameters;
    }

    public Application.Parameters getParameters() {
        return this.parameters;
    }

}
