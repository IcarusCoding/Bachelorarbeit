package de.intelligence.bachelorarbeit.simplifx.application;

import javafx.application.Application;
import javafx.stage.Stage;

import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.event.events.InitEvent;
import de.intelligence.bachelorarbeit.simplifx.event.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.event.events.StopEvent;

public final class ApplicationImpl extends Application {

    private final IEventEmitter emitter;

    public ApplicationImpl(IEventEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void init() {
        this.emitter.emit(new InitEvent());
    }

    @Override
    public void start(Stage stage) {
        this.emitter.emit(new StartEvent(stage));
        stage.show();
    }

    @Override
    public void stop() {
        this.emitter.emit(new StopEvent());
    }

}
