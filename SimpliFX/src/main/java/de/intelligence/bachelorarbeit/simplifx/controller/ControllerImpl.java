package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.layout.Pane;

class ControllerImpl implements IController {

    private final Object controllerInstance;
    private final Pane root;
    private final Class<?> controllerClass;
    private final ControllerVisibilityContext visibilityCtx;
    private final Map<String, IControllerGroup> subGroups;

    ControllerImpl(Object controllerInstance, Pane root) {
        this.controllerInstance = controllerInstance;
        this.root = root;
        this.controllerClass = controllerInstance.getClass();
        this.visibilityCtx = new ControllerVisibilityContext();
        this.subGroups = new HashMap<>();
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
    public ControllerVisibilityContext getVisibilityContext() {
        return this.visibilityCtx;
    }

    @Override
    public Map<String, IControllerGroup> getSubGroups() {
        return this.subGroups;
    }

    @Override
    public void destroy() {

    }

}
