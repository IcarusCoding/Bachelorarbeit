package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Map;

import javafx.scene.layout.Pane;

public interface IController {

    Object getControllerInstance();

    Pane getRoot();

    Class<?> getControllerClass();

    ControllerVisibilityContext getVisibilityContext();

    Map<String, IControllerGroup> getSubGroups();

    void destroy();

}
