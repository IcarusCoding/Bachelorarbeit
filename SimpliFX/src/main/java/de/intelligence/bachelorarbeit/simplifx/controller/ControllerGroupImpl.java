package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import de.intelligence.bachelorarbeit.simplifx.application.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.config.PropertyRegistry;
import de.intelligence.bachelorarbeit.simplifx.controller.animation.DefaultWrapperAnimation;
import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.exception.ControllerDestructionException;
import de.intelligence.bachelorarbeit.simplifx.exception.InvalidControllerGroupDefinitionException;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedResources;
import de.intelligence.bachelorarbeit.simplifx.utils.AnnotationUtils;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;
import de.intelligence.bachelorarbeit.simplifx.utils.FXThreadUtils;

public final class ControllerGroupImpl implements IControllerGroup {

    private final String groupId;
    private final Class<?> startController;
    private final IControllerFactoryProvider provider;
    private final II18N ii18N;
    private final SharedResources resources;
    private final PropertyRegistry registry;
    private final Function<Pane, INotificationDialog> notificationHandler;
    private final Consumer<Pane> readyConsumer;

    private final ControllerCreator creator;
    private final Map<Class<?>, IController> loadedControllers;
    private final ObjectProperty<IControllerGroupWrapper> groupWrapper;
    private final ObjectProperty<IController> activeController;
    private final ControllerGroupContext groupCtx;
    private final ObjectProperty<VisibilityState> visibility;
    private final List<ChangeListener<?>> weakListeners;
    private final List<Destructible> destructibleList;

    private boolean initialized;

    public ControllerGroupImpl(String groupId, Class<?> startController, IControllerFactoryProvider provider, II18N ii18N,
                               SharedResources resources, PropertyRegistry registry,
                               Function<Pane, INotificationDialog> notificationHandler,
                               Consumer<Pane> readyConsumer, IController superController) {
        this.groupId = groupId;
        this.startController = startController;
        this.provider = provider;
        this.ii18N = ii18N;
        this.resources = resources;
        this.registry = registry;
        this.notificationHandler = notificationHandler;
        this.readyConsumer = readyConsumer;
        this.creator = new ControllerCreator(provider, ii18N, resources, registry);
        this.loadedControllers = new ConcurrentHashMap<>();
        this.groupWrapper = new SimpleObjectProperty<>(new ControllerGroupWrapperImpl(notificationHandler));
        this.activeController = new SimpleObjectProperty<>();
        this.groupCtx = new ControllerGroupContext(this);
        this.visibility = new SimpleObjectProperty<>(VisibilityState.UNDEFINED);
        this.weakListeners = new ArrayList<>();
        this.destructibleList = new ArrayList<>();
        this.destructibleList.add(this.groupCtx);
        this.destructibleList.add(this.groupWrapper.get());
        if (superController != null) {
            // group visibility is always equal to super controller visibility
            this.visibility.set(superController.visibilityProperty().get());
        } else {
            // there is no super controller -> this group is root element and therefore always shown
            this.visibility.set(VisibilityState.SHOWN);
        }
        ControllerRegistry.register(groupId, this.groupCtx);
        ControllerRegistry.addController(groupId, startController);
    }

    @Override
    public IController getOrConstructController(Class<?> clazz) {
        if (this.loadedControllers.containsKey(clazz)) {
            return this.loadedControllers.get(clazz);
        }
        return this.createController(clazz);
    }

    @Override
    public Pane start() {
        if (this.initialized) {
            throw new IllegalStateException("Controller group can only be started once!");
        }
        if (this.readyConsumer != null) {
            this.readyConsumer.accept(this.groupWrapper.get().getWrapper());
        }
        this.setController(this.getOrConstructController(this.startController), new DefaultWrapperAnimation());
        this.initialized = true;
        return this.groupWrapper.get().getWrapper();
    }

    @Override
    public void start(Stage primary) {
        Conditions.checkNull(primary, "stage must not be null.");
        final ChangeListener<Boolean> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal) {
                if (newVal) {
                    ControllerGroupImpl.this.start();
                    primary.showingProperty().removeListener(this);
                }
            }
        };
        this.weakListeners.add(listener);
        primary.showingProperty().addListener(new WeakChangeListener<>(listener));
        final Pane root = this.getOrConstructController(this.startController).getRoot();
        primary.setScene(new Scene(this.groupWrapper.get().getWrapper(), root.getPrefWidth(), root.getPrefHeight()));
    }

    @Override
    public void createSubGroup(Class<?> originController, Class<?> startController, String groupId, Consumer<Pane> readyConsumer,
                               Function<Pane, INotificationDialog> notificationHandler) {
        if (this.initialized) {
            throw new IllegalStateException("Subgroups can only be registered in the setup phase!");
        }
        if (ControllerRegistry.isRegistered(startController)) {
            throw new InvalidControllerGroupDefinitionException("Controller \"" + startController.getSimpleName() + "\" is already registered in another group!");
        }
        if (ControllerRegistry.isRegistered(groupId)) {
            throw new InvalidControllerGroupDefinitionException("Group with id \"" + groupId + "\" is already registered!");
        }
        final IController origin = this.loadedControllers.get(originController);
        origin.getSubGroups().put(groupId, new ControllerGroupImpl(groupId, startController, this.provider, this.ii18N,
                this.resources, this.registry, notificationHandler == null ? this.notificationHandler : notificationHandler,
                readyConsumer, origin));
    }

    @Override
    public void switchController(Class<?> newController, IWrapperAnimation factory) {
        if (this.activeController.get() != null && !this.activeController.get().getControllerClass().equals(newController)) {
            this.setController(this.getOrConstructController(newController), factory);
        }
    }

    @Override
    public void destroy(Class<?> clazz) {
        if (this.loadedControllers.containsKey(clazz)) {
            ControllerRegistry.removeController(clazz);
            final IController controller = this.loadedControllers.remove(clazz);
            if (controller.getRoot() != null && controller.getRoot().getParent() != null && controller.getRoot().getParent() instanceof Pane) {
                ((Pane) controller.getRoot().getParent()).getChildren().remove(controller.getRoot());
            }
            AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), OnDestroy.class, OnDestroy::value);
            controller.destroy();
        }
    }

    @Override
    public void switchController(Class<?> newController) {
        this.switchController(newController, new DefaultWrapperAnimation());
    }

    @Override
    public ControllerGroupContext getContextFor(String groupId) {
        return ControllerRegistry.getContextFor(groupId);
    }

    @Override
    public Class<?> getActiveController() {
        return this.activeController.get().getControllerClass();
    }

    @Override
    public ObjectProperty<VisibilityState> visibilityProperty() {
        return this.visibility;
    }

    @Override
    public ControllerGroupContext getGroupContext() {
        return this.groupCtx;
    }

    @Override
    public Class<?> getStartControllerClass() {
        return this.startController;
    }

    @Override
    public void showNotification(StringBinding title, StringBinding content, NotificationKind kind) {
        this.groupWrapper.get().showNotification(title, content, kind);
    }

    @Override
    public String getGroupId() {
        return this.groupId;
    }

    @Override
    public void destroy() {
        this.loadedControllers.values().forEach(c -> c.getSubGroups().values().forEach(IControllerGroup::destroy));
        this.loadedControllers.keySet().forEach(this::destroy);
        if (!ControllerRegistry.removeGroup(this.groupId)) {
            throw new ControllerDestructionException("Could not destroy group " + this.groupId + ".");
        }
        this.groupWrapper.unbind();
        this.visibility.unbind();
        this.activeController.unbind();
        this.groupWrapper.set(null);
        this.visibility.set(VisibilityState.UNDEFINED);
        this.activeController.set(null);
        this.weakListeners.clear();
        this.destructibleList.forEach(Destructible::destroy);
    }

    private IController createController(Class<?> clazz) {
        if (!this.startController.equals(clazz) && ControllerRegistry.isRegistered(clazz)) {
            throw new InvalidControllerGroupDefinitionException("Controller \"" + clazz.getSimpleName() + "\" is already registered in another group!");
        }
        final IController controller = this.creator.createController(clazz);
        ControllerRegistry.addController(this.groupId, clazz);
        this.loadedControllers.put(clazz, controller);
        final ChangeListener<VisibilityState> listener = (obs, oldVal, newVal) -> {
            if (oldVal.type().equals(newVal.type())) {
                return;
            }
            if (newVal.type().equals(VisibilityState.SHOWN)) {
                controller.getVisibilityContext().incrementShow();
                AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), OnShow.class, OnShow::value,
                        true, controller.getVisibilityContext());
            } else if (newVal.type().equals(VisibilityState.HIDDEN)) {
                controller.getVisibilityContext().incrementHide();
                AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), OnHide.class, OnHide::value,
                        true, controller.getVisibilityContext());
            }
        };
        controller.registerWeakListener(listener);
        controller.visibilityProperty().addListener(new WeakChangeListener<>(listener));
        final ControllerSetupContext ctx = new ControllerSetupContext(clazz, this, this.groupCtx);
        this.destructibleList.add(ctx);
        final Object instance = controller.getControllerInstance();
        AnnotationUtils.invokeMethodsByAnnotation(instance, Setup.class, Setup::value, true, ctx);
        controller.getSubGroups().values().forEach(IControllerGroup::start);
        FXThreadUtils.waitOnFxThread(() -> AnnotationUtils.invokeMethodsByAnnotation(instance, PostConstruct.class, PostConstruct::value));
        return controller;
    }

    private void setController(IController controller, IWrapperAnimation animation) {
        if (controller.visibilityProperty().get().equals(VisibilityState.UNDEFINED)) {
            final ChangeListener<VisibilityState> visListener = (obs, oldVal, newVal) -> controller.getSubGroups().values()
                    .forEach(group -> {
                        group.visibilityProperty().bind(Bindings.createObjectBinding(() -> {
                            final VisibilityState controllerState = controller.visibilityProperty().get();
                            if (controllerState.type().equals(VisibilityState.HIDDEN)) {
                                return VisibilityState.GROUP_HIDDEN;
                            } else if (controllerState.type().equals(VisibilityState.SHOWN)) {
                                return VisibilityState.GROUP_SHOWN;
                            }
                            return VisibilityState.UNDEFINED;
                        }, controller.visibilityProperty()));
                    });
            controller.registerWeakListener(visListener);
            controller.visibilityProperty().addListener(new WeakChangeListener<>(visListener));
            final ChangeListener<VisibilityState> listener = (obs, oldVal, newVal) -> {
                if (this.activeController.get().equals(controller)) {
                    controller.visibilityProperty().set(newVal);
                }
            };
            controller.registerWeakListener(listener);
            this.visibility.addListener(new WeakChangeListener<>(listener));
        }
        if (this.activeController.get() == null) {
            final ChangeListener<IController> listener = (obs, oldVal, newVal) -> {
                if (this.visibility.get().equals(VisibilityState.UNDEFINED)) {
                    return;
                }
                if (oldVal != null) {
                    oldVal.visibilityProperty().set(VisibilityState.HIDDEN);
                }
                if (newVal != null) {
                    newVal.visibilityProperty().set(VisibilityState.SHOWN);
                }
            };
            this.weakListeners.add(listener);
            this.activeController.addListener(new WeakChangeListener<>(listener));
            this.activeController.set(controller);
            this.groupWrapper.get().setController(controller);
        } else {
            this.activeController.set(controller);
            this.groupWrapper.get().switchController(controller, animation);
        }
    }

}
