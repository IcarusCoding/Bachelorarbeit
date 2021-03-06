package de.intelligence.bachelorarbeit.simplifx.application;

import javax.inject.Inject;
import javax.inject.Named;

import javafx.application.Preloader;
import javafx.stage.Stage;

import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.events.ErrorEvent;
import de.intelligence.bachelorarbeit.simplifx.events.InitEvent;
import de.intelligence.bachelorarbeit.simplifx.events.PreloaderNotificationEvent;
import de.intelligence.bachelorarbeit.simplifx.events.ProgressEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StateChangeEvent;

/**
 * The {@link Preloader} implementation which will be used as the JavaFX preloader entrypoint.
 */
public final class PreloaderImpl extends Preloader {

    private final IEventEmitter emitter;

    @Inject
    public PreloaderImpl(@Named("preloaderEmitter") IEventEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void init() {
        this.emitter.emit(new InitEvent(null, super.getParameters()));
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

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        this.emitter.emit(new PreloaderNotificationEvent(info, super.getParameters()));
    }

    @Override
    public boolean handleErrorNotification(ErrorNotification info) {
        final ErrorEvent event = new ErrorEvent(info.getCause(), info.getDetails(), info.getLocation(),
                super.getParameters());
        this.emitter.emit(event);
        return event.wasHandled();
    }

}
