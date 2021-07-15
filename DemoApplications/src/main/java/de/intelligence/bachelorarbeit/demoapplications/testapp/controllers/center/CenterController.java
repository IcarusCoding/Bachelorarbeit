package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.center;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.left.LeftControllerOne;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.left.LeftControllerTwo;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.main.MainControllerTwo;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.right.RightControllerOne;
import de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.right.RightControllerTwo;
import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerGroupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.OnHide;
import de.intelligence.bachelorarbeit.simplifx.controller.OnShow;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;
import de.intelligence.bachelorarbeit.simplifx.controller.VisibilityContext;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.localization.ResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.shared.Shared;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedReference;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedResources;

@Controller(fxml = "/controllers/fxml/center/CenterController.fxml", css = "controllers/css/center.css")
public final class CenterController {

    @ResourceBundle
    private II18N language;

    @Shared
    private SharedResources resources;

    @Shared
    private SharedReference<String> testReference;

    @FXML
    private Button switchBtn;

    @FXML
    private Button switchLanguageBtn;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        final ControllerGroupContext mainCtx = ctx.getContextFor("main");
        final ControllerGroupContext leftCtx = ctx.getContextFor("left");
        final ControllerGroupContext rightCtx = ctx.getContextFor("right");
        final AtomicInteger i = new AtomicInteger();
        this.switchBtn.setOnAction(evt -> {
            if (i.incrementAndGet() == 3) {
                mainCtx.switchController(MainControllerTwo.class);
                return;
            }
            final Class<?> leftSwitch = leftCtx.getActiveController().equals(LeftControllerOne.class) ? LeftControllerTwo.class : LeftControllerOne.class;
            leftCtx.switchController(leftSwitch);
            final Class<?> rightSwitch = rightCtx.getActiveController().equals(RightControllerOne.class) ? RightControllerTwo.class : RightControllerOne.class;
            rightCtx.switchController(rightSwitch);
        });
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println("CenterController: Post-Construct!");
        System.out.println("RESOURCES (center): " + this.resources);
        System.out.println("BOOL REFERENCE (center): " + this.testReference.get());
        this.testReference.set("true");
        this.switchLanguageBtn.setOnAction(evt -> {
            if (this.language.getCurrentLocale().equals(Locale.GERMAN)) {
                this.language.setLocale(Locale.ENGLISH);
            } else {
                this.language.setLocale(Locale.GERMAN);
            }
        });
    }

    @OnShow
    private void onShow(VisibilityContext ctx) {
        System.out.println("CenterController: " + ctx.getVisibility());
    }

    @OnHide
    private void onHide(VisibilityContext ctx) {
        System.out.println("CenterController: " + ctx.getVisibility());
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("CenterController: Destruction!");
    }

}
