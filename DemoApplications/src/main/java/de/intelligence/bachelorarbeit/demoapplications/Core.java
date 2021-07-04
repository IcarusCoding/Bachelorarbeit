package de.intelligence.bachelorarbeit.demoapplications;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.stage.Stage;

import com.sun.javafx.application.PlatformImpl;

import lombok.extern.log4j.Log4j2;

import de.intelligence.bachelorarbeit.simplifx.ClasspathScanPolicy;
import de.intelligence.bachelorarbeit.simplifx.SimpliFX;
import de.intelligence.bachelorarbeit.simplifx.localization.CompoundResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.localization.I18N;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;

@Log4j2
public final class Core {

    static II18N ii18N = new I18N(Arrays.asList(new CompoundResourceBundle(Locale.GERMAN,
                    Collections.singletonList(ResourceBundle.getBundle("TestBundle", Locale.GERMAN))),
            new CompoundResourceBundle(Locale.ENGLISH,
                    Collections.singletonList(ResourceBundle.getBundle("TestBundle", Locale.ENGLISH)))));

    public static void main(String[] args) throws IOException {
       /* PlatformImpl.startup(() -> {
            Pane p = null;
            try {
                var loader = new SimpliFXMLLoader(Core.class.getResource("/speed.fxml"));
                loader.setII18N(ii18N);
                p = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ((Button) p.lookup("#testId")).setOnAction(e -> {
                if(ii18N.getCurrentLocale().equals(Locale.ENGLISH)) {
                    ii18N.setLocale(Locale.GERMAN);
                } else {
                    ii18N.setLocale(Locale.ENGLISH);
                }
            });
            Stage s = new Stage();
            Scene sc = new Scene(p);
            s.setScene(sc);
            s.show();
        });*/
        SimpliFX.setClasspathScanPolicy(ClasspathScanPolicy.LOCAL);
        SimpliFX.launch();
        //Application.launch(TestRealApp.class);

    }

    public static class TestRealApp extends Application {

        @Override
        public void init() throws Exception {
            System.out.println("I: " + Thread.currentThread());
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            System.out.println("S: " + Thread.currentThread());
            primaryStage.setOnCloseRequest(w -> PlatformImpl.exit());
            primaryStage.show();
        }

        @Override
        public void stop() throws Exception {
            System.out.println("ST: " + Thread.currentThread());
        }
    }

}
