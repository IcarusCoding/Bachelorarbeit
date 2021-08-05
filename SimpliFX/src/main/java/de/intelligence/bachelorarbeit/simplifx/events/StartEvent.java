package de.intelligence.bachelorarbeit.simplifx.events;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * An {@link AbstractApplicationEvent} for handling the {@link Application#start} method invocation.
 */
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
