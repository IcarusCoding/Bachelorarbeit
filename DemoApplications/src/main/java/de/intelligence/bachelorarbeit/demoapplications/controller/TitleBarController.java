package de.intelligence.bachelorarbeit.demoapplications.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;

import de.intelligence.bachelorarbeit.simplifx.application.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.localization.ResourceBundle;

@Controller(fxml = "/fxml/TitleBarController.fxml")
public final class TitleBarController {

    @FXML
    private Menu languageMenu;

    @ResourceBundle
    private II18N ii18N;

    @PostConstruct
    private void initMenu() {
        this.ii18N.setupMenu(this.languageMenu);
    }

}
