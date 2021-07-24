package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.config.ConfigValue;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;
import de.intelligence.bachelorarbeit.simplifx.controller.animation.FadeAnimation;
import de.intelligence.bachelorarbeit.simplifx.localization.LocalizeValue;

@Controller(fxml = "/fxml/SidebarController.fxml", css = "css/sidebarController.css")
public final class SidebarController {

    @FXML
    private Label connectionLbl;

    @ConfigValue("host")
    private String hostname;

    @ConfigValue("port")
    private int port;

    @LocalizeValue(id = "connectionLbl", property = "text")
    private StringProperty hostProperty = new SimpleStringProperty();

    @LocalizeValue(id = "connectionLbl", index = 1, property = "text")
    private IntegerProperty portProperty = new SimpleIntegerProperty();

    private ControllerSetupContext ctx;

    @Setup //TODO direct ctx injection
    private void onSetup(ControllerSetupContext ctx) {
        this.ctx = ctx;
    }

    @PostConstruct
    private void afterConstruction() {
        this.hostProperty.set(this.hostname);
        this.portProperty.set(this.port);
    }

    @FXML
    private void onLogoutPressed() {
        this.ctx.getContextFor("content").switchController(LoginController.class, new FadeAnimation(Duration.millis(250)));
    }

}
