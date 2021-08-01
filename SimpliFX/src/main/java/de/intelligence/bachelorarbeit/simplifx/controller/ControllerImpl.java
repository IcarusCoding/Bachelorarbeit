package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.Pane;

class ControllerImpl implements IController {

    private final Object controllerInstance;
    private final Pane root;
    private final Class<?> controllerClass;
    private final Map<String, IControllerGroup> subGroups;
    private final ObjectProperty<VisibilityState> visibility;
    private final VisibilityContext visibilityCtx;
    private final List<ChangeListener<?>> weakListeners;

    ControllerImpl(Object controllerInstance, Pane root) {
        this.controllerInstance = controllerInstance;
        this.root = root;
        this.controllerClass = controllerInstance.getClass();
        this.subGroups = new HashMap<>();
        this.visibility = new SimpleObjectProperty<>(VisibilityState.UNDEFINED);
        this.visibilityCtx = new VisibilityContext(this.visibility);
        this.weakListeners = new ArrayList<>();
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
        this.visibility.unbind();
        this.visibility.set(VisibilityState.UNDEFINED);
        this.subGroups.clear();
        this.weakListeners.clear();
    }

    @Override
    public ObjectProperty<VisibilityState> visibilityProperty() {
        return this.visibility;
    }

    @Override
    public VisibilityContext getVisibilityContext() {
        return this.visibilityCtx;
    }

    @Override
    public void registerWeakListener(ChangeListener<?> listener) {
        this.weakListeners.add(listener);
    }

}
