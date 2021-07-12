package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.right;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerGroupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;

@Controller(fxml = "/controllers/fxml/right/RightControllerOne.fxml", css = "controllers/css/rightOne.css")
public final class RightControllerOne {

    @FXML
    private Button switchBtn;

    @Setup
    private void setup(ControllerGroupContext ctx) {
        this.switchBtn.setOnAction(evt -> ctx.switchController(RightControllerTwo.class));
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("RightControllerOne: Post-Construct!");
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("RightControllerOne: Destruction!");
    }

}
