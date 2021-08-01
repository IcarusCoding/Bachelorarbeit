package de.intelligence.bachelorarbeit.simplifx.application;

import javax.inject.Inject;
import javax.inject.Named;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.stage.Stage;

import com.google.inject.assistedinject.Assisted;

import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.events.InitEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StopEvent;

public final class ApplicationImpl extends Application {

    private final IEventEmitter emitter;
    private final Preloader preloader;

    @Inject
    public ApplicationImpl(@Named("applicationEmitter") IEventEmitter emitter, @Assisted Preloader preloader) {
        this.emitter = emitter;
        this.preloader = preloader;
    }

    @Override
    public void init() {
        this.emitter.emit(new InitEvent(new PreloaderNotificationAccess(this.preloader), super.getParameters()));
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
