package de.intelligence.bachelorarbeit.simplifx.event.events;

import javafx.application.Application;

public final class InitEvent extends AbstractApplicationEvent {

    public InitEvent(Application.Parameters parameters) {
        super(parameters);
    }

}
