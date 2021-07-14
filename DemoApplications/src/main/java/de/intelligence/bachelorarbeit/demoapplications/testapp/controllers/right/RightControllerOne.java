package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.right;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.OnHide;
import de.intelligence.bachelorarbeit.simplifx.controller.OnShow;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;

@Controller(fxml = "/controllers/fxml/right/RightControllerOne.fxml", css = "controllers/css/rightOne.css")
public final class RightControllerOne {

    @FXML
    private Button switchBtn;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        this.switchBtn.setOnAction(evt -> ctx.switchController(RightControllerTwo.class));
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("RightControllerOne: Post-Construct!");
    }

    @OnShow
    private void onShow() {
        System.out.println("RightControllerOne: Shown!");
    }

    @OnHide
    private void onHide() {
        System.out.println("RightControllerOne: Hidden!");
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("RightControllerOne: Destruction!");
    }

}
