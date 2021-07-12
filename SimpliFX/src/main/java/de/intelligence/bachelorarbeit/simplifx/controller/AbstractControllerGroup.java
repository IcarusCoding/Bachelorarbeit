package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;

abstract class AbstractControllerGroup implements IControllerGroup {

    protected final Class<?> startController;
    protected final IControllerFactoryProvider provider;
    protected final II18N ii18N;
    protected final Consumer<Pane> readyConsumer;
    protected final String groupId;
    protected final ControllerGroupContext ctx;
    protected final ControllerCreator creator;
    protected final ObjectProperty<IController> activeHandler;
    protected final ObjectProperty<IControllerGroupWrapper> groupWrapper;
    protected final Map<Class<?>, IController> registeredControllers;
    protected final Map<String, IControllerGroup> subGroups;

    AbstractControllerGroup(Class<?> startController, IControllerFactoryProvider provider, II18N ii18N, Consumer<Pane> readyConsumer, String groupId) {
        this.startController = startController;
        this.provider = provider;
        this.ii18N = ii18N;
        this.readyConsumer = readyConsumer;
        this.groupId = groupId;
        this.ctx = new ControllerGroupContext(this);
        this.creator = new ControllerCreator(provider, ii18N, this.ctx);
        this.activeHandler = new SimpleObjectProperty<>();
        this.groupWrapper = new SimpleObjectProperty<>();
        this.registeredControllers = new ConcurrentHashMap<>();
        this.subGroups = new HashMap<>();
        ControllerRegistry.register(groupId, this.ctx);
        ControllerRegistry.addController(groupId, startController);
    }

    @Override
    public Class<?> getStartController() {
        return this.startController;
    }

    @Override
    public Class<?> getActiveController() {
        return this.activeHandler.get() == null ? null : this.activeHandler.get().getControllerClass();
    }

}
