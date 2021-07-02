package de.intelligence.bachelorarbeit.simplifx.event.events;

import javafx.application.Application;
import javafx.stage.Stage;

public final class StartEvent extends AbstractApplicationEvent {

    private final Stage stage;

    public StartEvent(Stage stage, Application.Parameters parameters) {
        super(parameters);
        this.stage = stage;
    }

    public Stage getStage() {
        return this.stage;
    }

}
