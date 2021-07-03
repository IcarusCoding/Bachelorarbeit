package de.intelligence.bachelorarbeit.demoapplications;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

import com.sun.javafx.application.PlatformImpl;

import lombok.extern.log4j.Log4j2;

import de.intelligence.bachelorarbeit.simplifx.ClasspathScanPolicy;
import de.intelligence.bachelorarbeit.simplifx.SimpliFX;

@Log4j2
public final class Core {


    public static void main(String[] args) throws IOException {
       /* PlatformImpl.startup(() -> {
            Pane p = null;
            try {
                var loader = new SimpliFXMLLoader(Core.class.getResource("/speed.fxml"));
                loader.setResources(ResourceBundle.getBundle("TestBundle"));
                loader.setTestInstance(testInstance);
                p = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ((Button) p.lookup("#testId")).setOnAction(e -> {
                if(testInstance.getCurrentLocale().equals(Locale.ENGLISH)) {
                    testInstance.setLocale(Locale.GERMAN);
                } else {
                    testInstance.setLocale(Locale.ENGLISH);
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
