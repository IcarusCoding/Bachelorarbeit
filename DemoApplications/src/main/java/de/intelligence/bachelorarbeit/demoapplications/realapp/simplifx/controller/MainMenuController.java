package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.controller;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnShow;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.localization.ResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.shared.Shared;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedReference;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedResources;

import static de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.controller.TestControllers.TestControllerOne;

@Controller(fxml = "/fxml/MainMenuController.fxml")
public final class MainMenuController {

    @FXML
    private BorderPane root;

    @FXML
    private StackPane contentCenter;

    @ResourceBundle
    private II18N ii18N;

    @Shared
    private SharedResources resources;

    private StringBinding binding;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        ctx.createSubGroup(SidebarController.class, "sidebar", this.root::setLeft);
        ctx.createSubGroup(TestControllerOne.class, "sidebarContent", this.contentCenter.getChildren()::setAll);
    }

    @PostConstruct
    private void afterConstruction() {
        this.binding = this.ii18N.createObservedBinding("mainMenu.welcome", this.resources.getForName("username").asProperty());
    }

    @OnShow
    private void onShow() {
        final SharedReference<StringProperty> prop = this.resources.getForName("title");
        prop.get().set(this.binding.get());
    }

}
