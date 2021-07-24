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
import de.intelligence.bachelorarbeit.simplifx.controller.animation.BottomSlideAnimation;

@Controller(fxml = "/legacy/controllers/fxml/right/RightControllerOne.fxml", css = "legacy/controllers/css/rightOne.css")
public final class RightControllerOne {

    @FXML
    private Button switchBtn;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        this.switchBtn.setOnAction(evt -> ctx.switchController(RightControllerTwo.class, new BottomSlideAnimation(Duration.millis(400))));
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("RightControllerOne: Post-Construct!");
    }

    @OnShow
    private void onShow(VisibilityContext ctx) {
        System.out.println("RightControllerOne: " + ctx.getVisibility());
    }

    @OnHide
    private void onHide(VisibilityContext ctx) {
        System.out.println("RightControllerOne: " + ctx.getVisibility());
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("RightControllerOne: Destruction!");
    }

}
