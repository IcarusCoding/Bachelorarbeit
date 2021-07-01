package de.intelligence.bachelorarbeit.demoapplications;

import javafx.application.Application;
import javafx.stage.Stage;

import com.sun.javafx.application.PlatformImpl;

import lombok.extern.log4j.Log4j2;

import de.intelligence.bachelorarbeit.simplifx.ClasspathScanPolicy;
import de.intelligence.bachelorarbeit.simplifx.SimpliFX;

@Log4j2
public final class Core {

    public static void main(String[] args) {
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
