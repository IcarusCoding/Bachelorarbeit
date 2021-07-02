package de.intelligence.bachelorarbeit.simplifx.application;

import javax.inject.Inject;
import javax.inject.Named;

import javafx.application.Preloader;
import javafx.stage.Stage;

import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.event.events.ErrorEvent;
import de.intelligence.bachelorarbeit.simplifx.event.events.InitEvent;
import de.intelligence.bachelorarbeit.simplifx.event.events.ProgressEvent;
import de.intelligence.bachelorarbeit.simplifx.event.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.event.events.StateChangeEvent;

public final class PreloaderImpl extends Preloader {

    private final IEventEmitter emitter;

    @Inject
    public PreloaderImpl(@Named("preloaderEmitter") IEventEmitter emitter) {
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
    public void handleProgressNotification(ProgressNotification info) {
        this.emitter.emit(new ProgressEvent(info.getProgress(), super.getParameters()));
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        this.emitter.emit(new StateChangeEvent(info.getType(), info.getApplication(), super.getParameters()));
    }

    //TODO let user call this somehow
    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        super.handleApplicationNotification(info);
    }

    @Override
    public boolean handleErrorNotification(ErrorNotification info) {
        this.emitter
                .emit(new ErrorEvent(info.getCause(), info.getDetails(), info.getLocation(), super.getParameters()));
        return true;
    }

}
