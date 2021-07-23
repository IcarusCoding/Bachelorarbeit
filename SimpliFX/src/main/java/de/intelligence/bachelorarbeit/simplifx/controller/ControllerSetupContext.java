package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.function.Consumer;

import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;

//TODO interfaces
public final class ControllerSetupContext {

    private final Class<?> controllerClass;
    private final IControllerGroup group;
    private final ControllerGroupContext groupCtx;

    public ControllerSetupContext(Class<?> controllerClass, IControllerGroup group, ControllerGroupContext groupCtx) {
        this.controllerClass = controllerClass;
        this.group = group;
        this.groupCtx = groupCtx;
    }

    public void createSubGroup(Class<?> clazz, String groupId, Consumer<Pane> readyConsumer) {
        this.group.createSubGroup(controllerClass, clazz, groupId, readyConsumer);
    }

    public void switchController(Class<?> clazz) {
        groupCtx.switchController(clazz);
    }

    public void switchController(Class<?> clazz, IWrapperAnimation factory) {
        groupCtx.switchController(clazz, factory);
    }

    public void preloadController(Class<?> clazz) {
        this.group.getOrConstructController(clazz);
    }

    public ControllerGroupContext getContextFor(String groupId) {
        return groupCtx.getContextFor(groupId);
    }

    public Class<?> getActiveController() {
        return groupCtx.getActiveController();
    }

}
