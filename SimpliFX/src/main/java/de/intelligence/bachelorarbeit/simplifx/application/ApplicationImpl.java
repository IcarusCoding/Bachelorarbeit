package de.intelligence.bachelorarbeit.simplifx.application;

import javax.inject.Inject;
import javax.inject.Named;

import javafx.application.Application;
import javafx.stage.Stage;

import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.events.InitEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StopEvent;

public final class ApplicationImpl extends Application {

    private final IEventEmitter emitter;

    @Inject
    public ApplicationImpl(@Named("applicationEmitter") IEventEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void init() {
        this.emitter.emit(new InitEvent(super.getParameters()));
    }

    @Override
    public void start(Stage stage) {
        this.emitter.emit(new StartEvent(stage, super.getParameters()));
    }

    @Override
    public void stop() {
        this.emitter.emit(new StopEvent(super.getParameters()));
    }

}
