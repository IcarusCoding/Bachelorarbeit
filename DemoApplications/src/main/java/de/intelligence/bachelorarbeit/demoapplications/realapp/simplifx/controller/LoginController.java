package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.controller;

import javax.inject.Inject;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.util.Duration;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.service.ILoginService;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerGroupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerSetupContext;
import de.intelligence.bachelorarbeit.simplifx.controller.OnHide;
import de.intelligence.bachelorarbeit.simplifx.controller.OnShow;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;
import de.intelligence.bachelorarbeit.simplifx.controller.animation.FadeAnimation;
import de.intelligence.bachelorarbeit.simplifx.shared.Shared;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedReference;

@Controller(fxml = "/fxml/LoginController.fxml", css = "css/loginController.css")
public final class LoginController {

    @FXML
    private JFXTextField usernameField;

    @FXML
    private JFXPasswordField passwordField;

    @Shared
    private SharedReference<String> usernameRef;

    @Shared
    private SharedReference<StringProperty> titleRef;

    @Inject
    private ILoginService loginService;

    private ControllerGroupContext ctx;

    @Setup
    private void setup(ControllerSetupContext ctx) {
        ctx.preloadController(MainMenuController.class);
        this.ctx = ctx.getGroupContext();
    }

    @OnShow
    private void onShow() {
        this.titleRef.get().set("Login");
    }

    @OnHide
    private void onHide() {
        this.usernameField.clear();
        this.passwordField.clear();
    }

    @FXML
    private void onLogin() {
        if (this.usernameField.validate() & this.passwordField.validate()) {
            if (this.loginService.login(this.usernameField.getText(), this.passwordField.getText())) {
                this.usernameRef.set(this.usernameField.getText());
                this.ctx.switchController(MainMenuController.class, new FadeAnimation(Duration.millis(250)));
            }
        }
    }

}
