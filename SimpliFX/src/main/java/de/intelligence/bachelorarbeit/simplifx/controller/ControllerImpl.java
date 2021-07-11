package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.scene.layout.Pane;

class ControllerImpl implements IController {

    private final Object controllerInstance;
    private final Pane root;
    private final Class<?> controllerClass;

    ControllerImpl(Object controllerInstance, Pane root) {
        this.controllerInstance = controllerInstance;
        this.root = root;
        this.controllerClass = controllerInstance.getClass();
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
    public void destroy() {

    }

}
