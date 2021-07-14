package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;

class ControllerImpl implements IController {

    private final Object controllerInstance;
    private final Pane root;
    private final Class<?> controllerClass;
    private final Map<String, IControllerGroup> subGroups;
    private final ObjectProperty<VisibilityState> visibility;
    private final VisibilityContext visibilityCtx;

    ControllerImpl(Object controllerInstance, Pane root) {
        this.controllerInstance = controllerInstance;
        this.root = root;
        this.controllerClass = controllerInstance.getClass();
        this.subGroups = new HashMap<>();
        this.visibility = new SimpleObjectProperty<>(VisibilityState.UNDEFINED);
        this.visibilityCtx = new VisibilityContext(this.visibility);
    }

    @Override
    public Object getControllerInstance() {
        return this.controllerInstance;
    }

    @Override
    public Pane getRoot() {
        return this.root;
    }

    @Override
    public Class<?> getControllerClass() {
        return this.controllerClass;
    }

    @Override
    public Map<String, IControllerGroup> getSubGroups() {
        return this.subGroups;
    }

    @Override
    public void destroy() {

    }

    @Override
    public ObjectProperty<VisibilityState> visibilityProperty() {
        return this.visibility;
    }

    @Override
    public VisibilityContext getVisibilityContext() {
        return this.visibilityCtx;
    }

}
