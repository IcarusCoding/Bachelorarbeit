package de.intelligence.bachelorarbeit.simplifx.event.events;

import javafx.application.Application;

public final class StopEvent extends AbstractApplicationEvent {

    public StopEvent(Application.Parameters parameters) {
        super(parameters);
    }

}
