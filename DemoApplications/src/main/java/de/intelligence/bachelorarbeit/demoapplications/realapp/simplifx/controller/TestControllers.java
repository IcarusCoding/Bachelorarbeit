package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.controller;

import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;

public final class TestControllers {

    @Controller(fxml = "/fxml/TestControllerOne.fxml")
    public static final class TestControllerOne {

        @Setup
        private void onSetup(ControllerSetupContext ctx) {
            ctx.preloadControllers(TestControllerTwo.class, TestControllerThree.class);
        }

    }

    @Controller(fxml = "/fxml/TestControllerTwo.fxml")
    public static final class TestControllerTwo {
    }

    @Controller(fxml = "/fxml/TestControllerThree.fxml")
    public static final class TestControllerThree {
    }

}
