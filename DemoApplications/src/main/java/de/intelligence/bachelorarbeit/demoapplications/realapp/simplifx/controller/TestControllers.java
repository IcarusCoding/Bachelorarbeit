package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.controller;

import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;

public final class TestControllers {

    @Controller(fxml = "/fxml/TestControllerOne.fxml", css = "css/testController.css")
    public static final class TestControllerOne {

        @Setup
        private void onSetup(ControllerSetupContext ctx) {
            ctx.preloadController(TestControllerTwo.class); //TODO support varargs
            ctx.preloadController(TestControllerThree.class);
        }

    }

    @Controller(fxml = "/fxml/TestControllerTwo.fxml", css = "css/testController.css")
    public static final class TestControllerTwo {
    }

    @Controller(fxml = "/fxml/TestControllerThree.fxml", css = "css/testController.css")
    public static final class TestControllerThree {
    }

}
