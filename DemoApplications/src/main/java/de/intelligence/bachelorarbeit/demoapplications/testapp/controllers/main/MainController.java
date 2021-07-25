package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.main;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.center.CenterController;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.left.LeftControllerOne;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.right.RightControllerOne;
import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.OnHide;
import de.intelligence.bachelorarbeit.simplifx.controller.OnShow;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;
import de.intelligence.bachelorarbeit.simplifx.controller.VisibilityContext;

@Controller(fxml = "/legacy/controllers/fxml/MainController.fxml", css = "legacy/controllers/css/main.css")
public final class MainController {

    @FXML
    private BorderPane root;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        ctx.createSubGroup(LeftControllerOne.class, "left", this.root::setLeft);
        ctx.createSubGroup(CenterController.class, "center", this.root::setCenter);
        ctx.createSubGroup(RightControllerOne.class, "right", this.root::setRight);
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("MainController: Post-Construct!");
    }

    @OnShow
    private void onShow(VisibilityContext ctx) {
        System.out.println("MainController: " + ctx.getVisibility());
    }

    @OnHide
    private void onHide(VisibilityContext ctx) {
        System.out.println("MainController: " + ctx.getVisibility());
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("MainController: Destruction!");
    }

}
