package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.main;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.center.CenterController;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.left.LeftControllerOne;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.right.RightControllerOne;
import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerGroupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;

@Controller(fxml = "/controllers/fxml/MainController.fxml", css = "controllers/css/main.css")
public final class MainController {

    @FXML
    private BorderPane root;

    @Setup
    private void setup(ControllerGroupContext ctx) {
        ctx.createSubGroupFor(LeftControllerOne.class, "left", root::setLeft);
        ctx.createSubGroupFor(CenterController.class, "center", root::setCenter);
        ctx.createSubGroupFor(RightControllerOne.class, "right", root::setRight);
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("MainController: Post-Construct!");
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("MainController: Destruction!");
    }

}
