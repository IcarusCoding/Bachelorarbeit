package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.right;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.OnHide;
import de.intelligence.bachelorarbeit.simplifx.controller.OnShow;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;
import de.intelligence.bachelorarbeit.simplifx.controller.VisibilityContext;
import de.intelligence.bachelorarbeit.simplifx.controller.animation.TopSlideAnimation;

@Controller(fxml = "/controllers/fxml/right/RightControllerTwo.fxml", css = "controllers/css/rightTwo.css")
public final class RightControllerTwo {

    @FXML
    private Button switchBtn;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        this.switchBtn.setOnAction(evt -> ctx.switchController(RightControllerOne.class, new TopSlideAnimation(Duration.millis(400))));
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("RightControllerTwo: Post-Construct!");
    }

    @OnShow
    private void onShow(VisibilityContext ctx) {
        System.out.println("RightControllerTwo: " + ctx.getVisibility());
    }

    @OnHide
    private void onHide(VisibilityContext ctx) {
        System.out.println("RightControllerTwo: " + ctx.getVisibility());
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("RightControllerTwo: Destruction!");
    }

}
