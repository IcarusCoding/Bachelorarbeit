package de.intelligence.bachelorarbeit.demoapplications;

import javafx.stage.StageStyle;

import de.intelligence.bachelorarbeit.simplifx.annotation.ApplicationEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.annotation.EventHandler;
import de.intelligence.bachelorarbeit.simplifx.annotation.StageConfig;
import de.intelligence.bachelorarbeit.simplifx.event.events.InitEvent;
import de.intelligence.bachelorarbeit.simplifx.event.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.event.events.StopEvent;

@StageConfig(title = "Test", style = StageStyle.DECORATED, alwaysTop = true,
        resizeable = false, iconPath = "/icogn.png")
@ApplicationEntryPoint(Core.class)
public final class TestApplication {

    @EventHandler
    private void onInit(InitEvent event) {
        System.out.println("Init event received! Thread: " + Thread.currentThread());
    }

    @EventHandler
    private void onStart(StartEvent event) {
        System.out.println("Start event received! Thread: " + Thread.currentThread());
        event.getStage().show();
    }

    @EventHandler
    private void onStop(StopEvent event) {
        System.out.println("Stop event received! Thread: " + Thread.currentThread());
    }

}
