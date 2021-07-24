package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.left;

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
import de.intelligence.bachelorarbeit.simplifx.controller.animation.BottomSlideAnimation;

@Controller(fxml = "/legacy/controllers/fxml/left/LeftControllerTwo.fxml", css = "legacy/controllers/css/leftTwo.css")
public final class LeftControllerTwo {

    @FXML
    private Button switchBtn;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        this.switchBtn.setOnAction(evt -> ctx.switchController(LeftControllerOne.class, new BottomSlideAnimation(Duration.millis(400))));
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("LeftControllerTwo: Post-Construct!");
    }

    @OnShow
    private void onShow(VisibilityContext ctx) {
        System.out.println("LeftControllerTwo: " + ctx.getVisibility());
    }

    @OnHide
    private void onHide(VisibilityContext ctx) {
        System.out.println("LeftControllerTwo: " + ctx.getVisibility());
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("LeftControllerTwo: Destruction!");
    }

}
