package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.center;

import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.left.LeftControllerOne;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.left.LeftControllerTwo;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.right.RightControllerOne;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.right.RightControllerTwo;
import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerGroupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.localization.ResourceBundle;

@Controller(fxml = "/controllers/fxml/center/CenterController.fxml", css = "controllers/css/center.css")
public final class CenterController {

    @ResourceBundle
    private II18N language;

    @FXML
    private Button switchBtn;

    @FXML
    private Button switchLanguageBtn;

    @Setup
    private void setup(ControllerGroupContext ctx) {
        final ControllerGroupContext leftCtx = ctx.getContextFor("left");
        final ControllerGroupContext rightCtx = ctx.getContextFor("right");
        this.switchBtn.setOnAction(evt -> {
            final Class<?> leftSwitch = leftCtx.getActiveController().equals(LeftControllerOne.class) ? LeftControllerTwo.class : LeftControllerOne.class;
            leftCtx.switchController(leftSwitch);
            final Class<?> rightSwitch = rightCtx.getActiveController().equals(RightControllerOne.class) ? RightControllerTwo.class : RightControllerOne.class;
            rightCtx.switchController(rightSwitch);
        });
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("CenterController: Post-Construct!");
        this.switchLanguageBtn.setOnAction(evt -> {
            if (this.language.getCurrentLocale().equals(Locale.GERMAN)) {
                this.language.setLocale(Locale.ENGLISH);
            } else {
                this.language.setLocale(Locale.GERMAN);
            }
        });
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("CenterController: Destruction!");
    }

}
