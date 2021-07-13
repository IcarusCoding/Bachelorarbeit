package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

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
    protected final ReadOnlyObjectWrapper<ControllerVisibilityContext.VisibilityState> visibility;
    protected final Map<String, IControllerGroup> currentSubGroups;

    protected IController parent;

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
        this.visibility = new ReadOnlyObjectWrapper<>(ControllerVisibilityContext.VisibilityState.UNDEFINED);
        this.visibility.addListener((obs, oldVal, newVal) -> {
            System.out.println("NEW STATE " + (groupId) + " -> " + newVal);
            if (this.activeHandler.get() != null) {
                this.activeHandler.get().getVisibilityContext().setState(newVal);
            }
        });
        this.currentSubGroups = new HashMap<>();
        //this.groupWrapper.addListener((obs, oldVal, newVal) -> this.visibility.set(ControllerVisibilityContext.VisibilityState.SHOWN));
        ControllerRegistry.register(groupId, this.ctx);
        ControllerRegistry.addController(groupId, startController);
    }

    @Override
    public void setParent(IController parent) {
        Conditions.checkNull(parent, "parent must not be null.");
        Conditions.checkCondition(this.parent == null, "parent was already set.");
        this.parent = parent;
        this.parent.getVisibilityContext().stateProperty().addListener((obs, oldVal, newVal) -> {
            // this.visibility.bind(this.parent.getVisibilityContext().stateProperty());
            if (newVal.equals(ControllerVisibilityContext.VisibilityState.HIDDEN) || newVal.equals(ControllerVisibilityContext.VisibilityState.GROUP_HIDDEN)) {
                this.visibility.set(ControllerVisibilityContext.VisibilityState.GROUP_HIDDEN);
            } else if (newVal.equals(ControllerVisibilityContext.VisibilityState.SHOWN)) {
                this.visibility.set(ControllerVisibilityContext.VisibilityState.SHOWN);
            }
        });
    }

    @Override
    public Class<?> getStartController() {
        return this.startController;
    }

    @Override
    public Class<?> getActiveController() {
        return this.activeHandler.get() == null ? null : this.activeHandler.get().getControllerClass();
    }

    @Override
    public ReadOnlyObjectProperty<ControllerVisibilityContext.VisibilityState> visibilityProperty() {
        return this.visibility.getReadOnlyProperty();
    }

}
