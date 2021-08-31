package de.intelligence.bachelorarbeit.simplifx.events;

import javafx.application.Application;

/**
 * An {@link AbstractApplicationEvent} for handling the {@link Application#stop} method invocation.
 */
public final class StopEvent extends AbstractApplicationEvent {

    public StopEvent(Application.Parameters parameters) {
        super(parameters);
    }

}
