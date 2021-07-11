package de.intelligence.bachelorarbeit.demoapplications;

import javafx.application.Preloader;
import javafx.stage.Stage;

import de.intelligence.bachelorarbeit.simplifx.annotation.EventHandler;
import de.intelligence.bachelorarbeit.simplifx.application.PreloaderEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.application.StageConfig;
import de.intelligence.bachelorarbeit.simplifx.events.InitEvent;
import de.intelligence.bachelorarbeit.simplifx.events.ProgressEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StateChangeEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StopEvent;

@StageConfig(title = "TestPreloader")
@PreloaderEntryPoint
public final class TestPreloader {

    private Stage stage;

    @EventHandler
    private void onInit(InitEvent event) {
        System.out.println("Pre Init event received! Thread: " + Thread.currentThread());
    }

    @EventHandler
    private void onStart(StartEvent event) {
        System.out.println("Pre Start event received! Thread: " + Thread.currentThread());
        this.stage = event.getStage();
        this.stage.show();
    }

    @EventHandler
    private void onStop(StopEvent event) {
        System.out.println("Pre Stop event received! Thread: " + Thread.currentThread());
    }

    @EventHandler
    private void onStateChanged(StateChangeEvent event) {
        System.out.println("Pre StateChangeEvent received! Thread: " + Thread.currentThread() + " " + event.getType());
        if (event.getType() == Preloader.StateChangeNotification.Type.BEFORE_START) {
            this.stage.hide();
        }
    }

    @EventHandler
    private void onProgress(ProgressEvent event) {
        System.out.println("Pre ProgressEvent received! Thread: " + Thread.currentThread() + " " + event.getProgress());
    }

}
