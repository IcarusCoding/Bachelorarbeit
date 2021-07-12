package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.function.Consumer;

import javafx.scene.layout.Pane;

//TODO add previous history
public final class ControllerGroupContext {

    private final IControllerGroup group;

    public ControllerGroupContext(IControllerGroup group) {
        this.group = group;
    }

    //TODO maybe group into annotation to enable dynamic pre initialization
    public void createSubGroupFor(Class<?> clazz, String groupId, Consumer<Pane> readyConsumer) {
        this.group.registerSubGroup(clazz, groupId, readyConsumer);
    }

    public void switchController(Class<?> clazz) {
        this.group.switchController(clazz);
    }

    public void switchController(Class<?> clazz, IWrapperAnimationFactory factory) {
        this.group.switchController(clazz, factory);
    }

    public ControllerGroupContext getContextFor(String groupId) {
        return this.group.getContextFor(groupId);
    }

    public Class<?> getActiveController() {
        return this.group.getActiveController();
    }

}
