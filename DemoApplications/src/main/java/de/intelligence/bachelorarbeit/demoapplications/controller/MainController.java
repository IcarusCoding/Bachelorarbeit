package de.intelligence.bachelorarbeit.demoapplications.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;

@Controller(fxml = "/fxml/MainController.fxml", css = "css/main.css")
public final class MainController {

    @FXML
    private BorderPane root;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        ctx.createSubGroup(TitleBarController.class, "titleBar", this.root::setTop);
        ctx.createSubGroup(LoginController.class, "mainContent", this.root::setCenter);
    }

}
