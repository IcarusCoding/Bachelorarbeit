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
import de.intelligence.bachelorarbeit.simplifx.controller.animation.TopSlideAnimation;

@Controller(fxml = "/legacy/controllers/fxml/left/LeftControllerOne.fxml", css = "legacy/controllers/css/leftOne.css")
public final class LeftControllerOne {

    @FXML
    private Button switchBtn;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        this.switchBtn.setOnAction(evt -> ctx.switchController(LeftControllerTwo.class, new TopSlideAnimation(Duration.millis(400))));
        ctx.createSubGroup(TestController.class, "legacy/test", p -> {
            System.out.println("READY");
        });
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("LeftControllerOne: Post-Construct!");
    }

    @OnShow
    private void onShow(VisibilityContext ctx) {
        System.out.println("LeftControllerOne: " + ctx.getVisibility());
    }

    @OnHide
    private void onHide(VisibilityContext ctx) {
        System.out.println("LeftControllerOne: " + ctx.getVisibility());
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("LeftControllerOne: Destruction!");
    }

}
