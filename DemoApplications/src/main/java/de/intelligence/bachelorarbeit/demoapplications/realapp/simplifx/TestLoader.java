package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx;

import javafx.application.Preloader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import de.intelligence.bachelorarbeit.simplifx.application.PreloaderEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.application.StageConfig;
import de.intelligence.bachelorarbeit.simplifx.event.EventHandler;
import de.intelligence.bachelorarbeit.simplifx.events.PreloaderNotificationEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StateChangeEvent;

@StageConfig(title = "Preloader", style = StageStyle.DECORATED, icons = "/icon/icon.png", autoShow = true)
@PreloaderEntryPoint
public final class TestLoader {

    private Stage stage;

    @EventHandler
    private void onStart(StartEvent event) {
        this.stage = event.getStage();
    }

    @EventHandler
    private void onStateChanged(StateChangeEvent event) {
        if (event.getType() == Preloader.StateChangeNotification.Type.BEFORE_START) {
            this.stage.hide();
        }
    }

    @EventHandler
    private void onPreloaderNotification(PreloaderNotificationEvent event) {
        System.out.println("SUCCESSFULLY RECEIVED " + event.getNotification());
    }

}
