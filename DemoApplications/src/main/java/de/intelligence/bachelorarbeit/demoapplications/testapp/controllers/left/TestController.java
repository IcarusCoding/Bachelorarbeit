package de.intelligence.bachelorarbeit.demoapplications.testapp.controllers.left;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.OnHide;
import de.intelligence.bachelorarbeit.simplifx.controller.OnShow;
import de.intelligence.bachelorarbeit.simplifx.controller.VisibilityContext;

@Controller(fxml = "/controllers/fxml/left/LeftControllerTwo.fxml", css = "controllers/css/leftTwo.css")
public class TestController {

    @PostConstruct
    private void afterConstruction() {
        System.out.println("SUBSUB: Post-Construct!");
    }

    @OnShow
    private void onShow(VisibilityContext ctx) {
        System.out.println("SUBSUB: " + ctx.getVisibility());
    }

    @OnHide
    private void onHide(VisibilityContext ctx) {
        System.out.println("SUBSUB: " + ctx.getVisibility());
    }

    @OnDestroy
    private void onDestroy() {
        System.out.println("SUBSUB: Destruction!");
    }

}
