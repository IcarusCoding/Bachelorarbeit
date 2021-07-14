package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;

public interface IController {

    Object getControllerInstance();

    Pane getRoot();

    Class<?> getControllerClass();

    Map<String, IControllerGroup> getSubGroups();

    void destroy();

    ObjectProperty<VisibilityState> visibilityProperty();

    VisibilityContext getVisibilityContext();

}
