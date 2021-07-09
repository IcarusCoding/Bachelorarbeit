package de.intelligence.bachelorarbeit.demoapplications;

import javax.inject.Inject;
import java.io.IOException;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import de.intelligence.bachelorarbeit.simplifx.annotation.EventHandler;
import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.application.ApplicationEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.application.StageConfig;
import de.intelligence.bachelorarbeit.simplifx.events.InitEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StopEvent;
import de.intelligence.bachelorarbeit.simplifx.fxml.SimpliFXMLLoader;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.localization.ResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.spring.SpringInjection;

@StageConfig(title = "Test", style = StageStyle.DECORATED, alwaysTop = true,
        resizeable = false, iconPath = "/icon.png")
@ApplicationEntryPoint(Core.class)
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
        //System.out.println("Post Construct: " + language.get("test.key") + " " + language.get("test.key2"));
        service.test();
    }

    @EventHandler
    private void onInit(InitEvent event) {
        System.out.println("Init event received! Thread: " + Thread.currentThread());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    private void onStart(StartEvent event) {
        System.out.println("Start event received! Thread: " + Thread.currentThread());
        final Stage testStage = event.getStage();
        // testing
        final SimpliFXMLLoader loader = new SimpliFXMLLoader(getClass().getResource("/test.fxml"));
        loader.setII18N(language2);
        try {
            Pane p = loader.load();
            p.getStylesheets().add("test.css");
            Scene scene = new Scene(p);
            testStage.setScene(scene);
            testStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    private void onStop(StopEvent event) {
        System.out.println("Stop event received! Thread: " + Thread.currentThread());
    }

}
