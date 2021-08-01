package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx;

import java.util.Properties;

import javafx.application.Preloader;
import javafx.beans.property.StringProperty;

import com.jfoenix.skins.JFXTextFieldSkin;

import de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.controller.MainController;
import de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.di.MainModule;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.SimpliFX;
import de.intelligence.bachelorarbeit.simplifx.application.ApplicationEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.application.StageConfig;
import de.intelligence.bachelorarbeit.simplifx.config.ConfigSource;
import de.intelligence.bachelorarbeit.simplifx.event.EventHandler;
import de.intelligence.bachelorarbeit.simplifx.events.InitEvent;
import de.intelligence.bachelorarbeit.simplifx.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.guice.GuiceInjection;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.localization.ResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.shared.Shared;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedReference;

@StageConfig(title = "Login", icons = "/icon/icon.png")
@ApplicationEntryPoint(MainController.class)
@GuiceInjection(MainModule.class)
public final class DemoApplication {

    public static void main(String[] args) throws Exception {
        Reflection.addOpens("java.lang.reflect", "java.base", JFXTextFieldSkin.class.getModule());
        SimpliFX.launchWithPreloader();
    }

    @ResourceBundle("lang.Messages")
    private II18N ii18N;

    @ConfigSource("config/connection")
    private Properties properties;

    @Shared
    private SharedReference<StringProperty> titleRef;

    @EventHandler
    private void onInit(InitEvent event) {
        event.getNotificationAccess().notifyPreloader(new Preloader.ErrorNotification(null, "TEST", null));
       /* try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    @EventHandler
    private void onStart(StartEvent event) {
        this.titleRef.set(event.getStage().titleProperty());
        event.getStage().show();
    }

}
