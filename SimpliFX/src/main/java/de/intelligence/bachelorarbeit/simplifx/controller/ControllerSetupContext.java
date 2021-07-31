package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.binding.StringBinding;
import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;

//TODO interfaces
public final class ControllerSetupContext implements Destructible {

    private final Class<?> controllerClass;
    private final ControllerGroupContext groupCtx;
    private IControllerGroup group;

    public ControllerSetupContext(Class<?> controllerClass, IControllerGroup group, ControllerGroupContext groupCtx) {
        this.controllerClass = controllerClass;
        this.group = group;
        this.groupCtx = groupCtx;
    }

    public ControllerGroupContext getGroupContext() {
        return this.groupCtx;
    }

    public void createSubGroup(Class<?> clazz, String groupId, Consumer<Pane> readyConsumer,
                               Function<Pane, INotificationDialog> notificationHandler) {
        if (this.group != null) {
            this.group.createSubGroup(this.controllerClass, clazz, groupId, readyConsumer, notificationHandler);
        }
    }

    public void createSubGroup(Class<?> clazz, String groupId, Consumer<Pane> readyConsumer) {
        if (this.group != null) {
            this.group.createSubGroup(this.controllerClass, clazz, groupId, readyConsumer, null);
        }
    }

    public void preloadControllers(Class<?>... clazz) {
        if (this.group != null) {
            Arrays.stream(clazz).forEach(this::preloadController);
        }
    }

    public void preloadController(Class<?> clazz) {
        if (this.group != null) {
            this.group.getOrConstructController(clazz);
        }
    }

    public void showNotification(String title, String content, NotificationKind kind) {
        this.groupCtx.showNotification(title, content, kind);
    }

    public void showNotification(StringBinding title, StringBinding content, NotificationKind kind) {
        this.groupCtx.showNotification(title, content, kind);
    }

    public void switchController(Class<?> clazz) {
        this.groupCtx.switchController(clazz);
    }

    public void switchController(Class<?> clazz, IWrapperAnimation factory) {
        this.groupCtx.switchController(clazz, factory);
    }

    public ControllerGroupContext getContextFor(String groupId) {
        return this.groupCtx.getContextFor(groupId);
    }

    public Class<?> getActiveController() {
        return this.groupCtx.getActiveController();
    }

    @Override
    public void destroy() {
        this.group = null;
    }

}
