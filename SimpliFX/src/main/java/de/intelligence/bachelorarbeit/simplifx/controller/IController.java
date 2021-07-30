package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.Pane;

public interface IController extends Destructible {

    Object getControllerInstance();

    Pane getRoot();

    Class<?> getControllerClass();

    Map<String, IControllerGroup> getSubGroups();

    ObjectProperty<VisibilityState> visibilityProperty();

    VisibilityContext getVisibilityContext();

    void registerWeakListener(ChangeListener<?> listener);

}
