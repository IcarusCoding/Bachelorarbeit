package de.intelligence.bachelorarbeit.simplifx.event.events;

import javafx.stage.Stage;

public final class StartEvent extends AbstractEvent {

    private final Stage stage;

    public StartEvent(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return this.stage;
    }

}
