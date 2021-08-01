package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;

import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;

public final class ControllerGroupContext implements Destructible {

    private IControllerGroup group;

    public ControllerGroupContext(IControllerGroup group) {
        this.group = group;
    }

    public void switchController(Class<?> clazz) {
        if (this.group != null) {
            this.group.switchController(clazz);
        }
    }

    public void switchController(Class<?> clazz, IWrapperAnimation factory) {
        if (this.group != null) {
            this.group.switchController(clazz, factory);
        }
    }

    public ControllerGroupContext getContextFor(String groupId) {
        if (this.group != null) {
            return this.group.getContextFor(groupId);
        }
        return null;
    }

    public Class<?> getActiveController() {
        if (this.group != null) {
            return this.group.getActiveController();
        }
        return null;
    }

    public void showNotification(String title, String content, NotificationKind kind) {
        this.showNotification(Bindings.createStringBinding(() -> title), Bindings.createStringBinding(() -> content), kind);
    }

    public void showNotification(StringBinding title, StringBinding content, NotificationKind kind) {
        if (this.group != null) {
            this.group.showNotification(title, content, kind);
        }
    }

    public void destroyController(Class<?> clazz) {
        if (this.group != null) {
            this.group.destroy(clazz);
        }
    }

    public void destroyGroup() {
        if (this.group != null) {
            this.group.destroy();
        }
    }

    @Override
    public void destroy() {
        this.group = null;
    }

}