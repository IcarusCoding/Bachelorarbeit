package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.controller;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnHide;
import de.intelligence.bachelorarbeit.simplifx.controller.OnShow;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.localization.ResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.shared.Shared;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedReference;

@Controller(fxml = "/fxml/MainMenuController.fxml")
public final class MainMenuController {

    @FXML
    private BorderPane root;

    @ResourceBundle
    private II18N ii18N;

    @Shared
    private SharedReference<String> usernameRef;

    @Shared
    private SharedReference<StringProperty> titleRef;

    private StringBinding binding;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        ctx.createSubGroup(SidebarController.class, "sidebar", this.root::setLeft);
        ctx.createSubGroup(ContentController.class, "sidebarContent", this.root::setCenter);
    }

    @PostConstruct
    private void afterConstruction() {
        this.binding = this.ii18N.createObservedBinding("mainMenu.welcome", this.usernameRef.asProperty());
    }

    @OnShow
    private void onShow() {
        this.titleRef.get().bind(this.binding);
    }

    @OnHide
    private void onHide() {
        this.titleRef.get().unbind();
    }

}
