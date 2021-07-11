package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.scene.layout.Pane;

public interface IController {

    Object getControllerInstance();

    Pane getRoot();

    Class<?> getControllerClass();

    void destroy();

}
