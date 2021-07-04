package de.intelligence.bachelorarbeit.demoapplications;

import java.io.IOException;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import de.intelligence.bachelorarbeit.simplifx.annotation.ApplicationEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.annotation.EventHandler;
import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.annotation.ResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.annotation.StageConfig;
import de.intelligence.bachelorarbeit.simplifx.event.events.InitEvent;
import de.intelligence.bachelorarbeit.simplifx.event.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.event.events.StopEvent;
import de.intelligence.bachelorarbeit.simplifx.fxml.SimpliFXMLLoader;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;

@StageConfig(title = "Test", style = StageStyle.DECORATED, alwaysTop = true,
        resizeable = false, iconPath = "/icon.png")
@ApplicationEntryPoint(Core.class)
public final class TestApplication {

    @ResourceBundle("TestBundle")
    private II18N language;

    @ResourceBundle("test/Messages")
    private II18N language2;

    @PostConstruct
    private void postConstructTest() {
        //System.out.println("Post Construct: " + language.get("test.key") + " " + language.get("test.key2"));
    }

    @EventHandler
    private void onInit(InitEvent event) {
        System.out.println("Init event received! Thread: " + Thread.currentThread());
    }

    @EventHandler
    private void onStart(StartEvent event) {
        System.out.println("Start event received! Thread: " + Thread.currentThread());
        final Stage testStage = event.getStage();
        // testing
        final SimpliFXMLLoader loader = new SimpliFXMLLoader(getClass().getResource("/test.fxml"));
        loader.setII18N(language);
        try {
            Pane p = loader.load();
            Scene scene = new Scene(p);
            testStage.setScene(scene);
            testStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*StringProperty keyProp = new SimpleStringProperty("test.key");
        IntegerProperty property = new SimpleIntegerProperty(42);
        StringBinding binding = language.createObservedBinding("test.key", property);
        System.out.println("BEFORE: " + binding.get());
        language.setLocale(Locale.GERMAN);
        System.out.println("AFTER: " + binding.get());
        property.set(33);
        System.out.println("AFTER 2: " + binding.get());*/
    }

    @EventHandler
    private void onStop(StopEvent event) {
        System.out.println("Stop event received! Thread: " + Thread.currentThread());
    }

}
