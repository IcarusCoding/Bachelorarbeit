package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

abstract class AbstractControllerGroup implements IControllerGroup {

    protected final Class<?> startController;
    protected final ControllerCreator creator;
    protected final ObjectProperty<IController> activeHandler;
    protected final ObjectProperty<IControllerGroupWrapper> groupWrapper;
    protected final Map<Class<?>, IController> registeredControllers;

    AbstractControllerGroup(Class<?> startController, ControllerCreator creator) {
        this.startController = startController;
        this.creator = creator;
        this.activeHandler = new SimpleObjectProperty<>();
        this.groupWrapper = new SimpleObjectProperty<>();
        this.registeredControllers = new ConcurrentHashMap<>();
    }

    @Override
    public Class<?> getStartController() {
        return this.startController;
    }

}
