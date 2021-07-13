package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.OnHide;
import de.intelligence.bachelorarbeit.simplifx.controller.OnShow;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;
import de.intelligence.bachelorarbeit.simplifx.realC.ControllerSetupContext;

@Controller(fxml = "/controllers/fxml/MainControllerTwo.fxml", css = "controllers/css/main.css")
public final class MainControllerTwo {

    @FXML
    private Button testBtn;

    private ControllerSetupContext ctx;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        this.ctx = ctx;
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("MainControllerTwo: Post-Construct!");
        this.testBtn.setOnAction(e -> this.ctx.switchController(MainController.class));
    }

    @OnShow
    private void onShow() {
        System.out.println("MainControllerTwo: Shown!");
    }

    @OnHide
    private void onHide() {
        System.out.println("MainControllerTwo: Hidden!");
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("MainControllerTwo: Destruction!");
    }

}
