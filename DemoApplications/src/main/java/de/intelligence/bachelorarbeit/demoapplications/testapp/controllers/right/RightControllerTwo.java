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

@Controller(fxml = "/controllers/fxml/right/RightControllerTwo.fxml", css = "controllers/css/rightTwo.css")
public final class RightControllerTwo {

    @FXML
    private Button switchBtn;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        this.switchBtn.setOnAction(evt -> ctx.switchController(RightControllerOne.class));
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("RightControllerTwo: Post-Construct!");
    }

    @OnShow
    private void onShow() {
        System.out.println("RightControllerTwo: Shown!");
    }

    @OnHide
    private void onHide() {
        System.out.println("RightControllerTwo: Hidden!");
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("RightControllerTwo: Destruction!");
    }

}
