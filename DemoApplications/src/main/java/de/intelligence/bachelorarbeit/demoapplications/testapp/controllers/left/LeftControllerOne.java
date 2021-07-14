package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.left;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.OnHide;
import de.intelligence.bachelorarbeit.simplifx.controller.OnShow;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;

@Controller(fxml = "/controllers/fxml/left/LeftControllerOne.fxml", css = "controllers/css/leftOne.css")
public final class LeftControllerOne {

    @FXML
    private Button switchBtn;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        this.switchBtn.setOnAction(evt -> ctx.switchController(LeftControllerTwo.class));
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("LeftControllerOne: Post-Construct!");
    }

    @OnShow
    private void onShow() {
        System.out.println("LeftControllerOne: Shown!");
    }

    @OnHide
    private void onHide() {
        System.out.println("LeftControllerOne: Hidden!");
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("LeftControllerOne: Destruction!");
    }

}
