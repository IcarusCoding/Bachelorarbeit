package de.intelligence.bachelorarbeit.simplifx.events;

import javafx.application.Application;

/**
 * A basic event which currently only encapsulates the global {@link Application.Parameters} for the created application instance.
 */
public abstract class AbstractApplicationEvent {

    private final Application.Parameters parameters;

    protected AbstractApplicationEvent(Application.Parameters parameters) {
        this.parameters = parameters;
    }

    public Application.Parameters getParameters() {
        return this.parameters;
    }

}
