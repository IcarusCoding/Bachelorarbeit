package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx;

import java.util.List;
import java.util.Properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.StageStyle;

import com.jfoenix.skins.JFXTextFieldSkin;

import de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.controller.MainController;
import de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.di.MainModule;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.SimpliFX;
import de.intelligence.bachelorarbeit.simplifx.annotation.EventHandler;
import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.application.ApplicationEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.application.StageConfig;
import de.intelligence.bachelorarbeit.simplifx.config.ConfigSource;
import de.intelligence.bachelorarbeit.simplifx.events.StartEvent;
import de.intelligence.bachelorarbeit.simplifx.guice.GuiceInjection;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.localization.ResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.shared.Shared;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedReference;

@StageConfig(title = "Login", style = StageStyle.DECORATED, resizeable = false, iconPath = "/icon/icon.png")
@ApplicationEntryPoint(MainController.class)
@GuiceInjection(MainModule.class)
public final class DemoApplication {

    public static void main(String[] args) throws Exception {
        Reflection.addOpens(List.of("java.lang.reflect"), "java.base", JFXTextFieldSkin.class.getModule());
        SimpliFX.launchWithPreloader();
    }

    @ResourceBundle("lang.Messages")
    private II18N ii18N;

    @ConfigSource("config/connection")
    private Properties properties;

    @Shared
    private SharedReference<StringProperty> titleRef;

    @PostConstruct
    private void afterConstruction() {
        this.titleRef.set(new SimpleStringProperty());
    }

    @EventHandler
    private void onStart(StartEvent event) {
        this.titleRef.set(event.getStage().titleProperty());
        event.getStage().show();
    }

}
