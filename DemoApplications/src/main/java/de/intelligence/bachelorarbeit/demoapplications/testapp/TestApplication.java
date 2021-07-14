package de.intelligence.bachelorarbeit.demoapplications.testapp;

import javax.inject.Inject;

import javafx.stage.StageStyle;

import de.intelligence.bachelorarbeit.demoapplications.ITestService;
import de.intelligence.bachelorarbeit.demoapplications.TestSpringModule;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.main.MainController;
import de.intelligence.bachelorarbeit.simplifx.annotation.EventHandler;
import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.application.ApplicationEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.application.StageConfig;
import de.intelligence.bachelorarbeit.simplifx.events.InitEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StopEvent;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.localization.ResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.spring.SpringInjection;

@StageConfig(title = "Test", style = StageStyle.DECORATED, alwaysTop = true,
        resizeable = false, iconPath = "/icon.png")
@ApplicationEntryPoint(MainController.class)
@SpringInjection(TestSpringModule.class)
public final class TestApplication {

    @Inject
    private ITestService service;

    @ResourceBundle("TestBundle")
    private II18N language;

    @ResourceBundle("test/Messages")
    private II18N language2;

    @PostConstruct
    private void postConstructTest() {
        service.test();
    }

    @EventHandler
    private void onInit(InitEvent event) {
        System.out.println("Init event received! Thread: " + Thread.currentThread());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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