package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.left;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerGroupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;

@Controller(fxml = "/controllers/fxml/left/LeftControllerTwo.fxml", css = "controllers/css/leftTwo.css")
public final class LeftControllerTwo {

    @FXML
    private Button switchBtn;

    @Setup
    private void setup(ControllerGroupContext ctx) {
        this.switchBtn.setOnAction(evt -> ctx.switchController(LeftControllerOne.class));
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("LeftControllerTwo: Post-Construct!");
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("LeftControllerTwo: Destruction!");
    }

}
