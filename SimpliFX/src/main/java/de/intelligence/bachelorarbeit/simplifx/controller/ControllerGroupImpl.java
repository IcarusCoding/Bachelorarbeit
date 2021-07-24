package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.config.PropertyRegistry;
import de.intelligence.bachelorarbeit.simplifx.controller.animation.DefaultWrapperAnimation;
import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.exception.InvalidControllerGroupDefinitionException;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedResources;
import de.intelligence.bachelorarbeit.simplifx.utils.AnnotationUtils;
import de.intelligence.bachelorarbeit.simplifx.utils.FXThreadUtils;

public final class ControllerGroupImpl implements IControllerGroup {

    private final String groupId;
    private final Class<?> startController;
    private final IControllerFactoryProvider provider;
    private final II18N ii18N;
    private final SharedResources resources;
    private final PropertyRegistry registry;
    private final Consumer<Pane> readyConsumer;
    private final IController superController;

    private final ControllerCreator creator;
    private final Map<Class<?>, IController> loadedControllers;
    private final ObjectProperty<IControllerGroupWrapper> groupWrapper;
    private final ObjectProperty<IController> activeController;
    private final ControllerGroupContext groupCtx;
    private final ObjectProperty<VisibilityState> visibility;
    private boolean initialized;

    public ControllerGroupImpl(String groupId, Class<?> startController, IControllerFactoryProvider provider, II18N ii18N,
                               SharedResources resources, PropertyRegistry registry, Consumer<Pane> readyConsumer, IController superController) {
        this.groupId = groupId;
        this.startController = startController;
        this.provider = provider;
        this.ii18N = ii18N;
        this.resources = resources;
        this.registry = registry;
        this.readyConsumer = readyConsumer;
        this.superController = superController;
        this.creator = new ControllerCreator(provider, ii18N, resources, registry);
        this.loadedControllers = new ConcurrentHashMap<>();
        this.groupWrapper = new SimpleObjectProperty<>(new ControllerGroupWrapperImpl());
        this.activeController = new SimpleObjectProperty<>();
        this.groupCtx = new ControllerGroupContext(this);
        this.visibility = new SimpleObjectProperty<>(VisibilityState.UNDEFINED);
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
        if (this.readyConsumer != null) {
            this.readyConsumer.accept(this.groupWrapper.get().getWrapper());
        }
        this.setController(this.getOrConstructController(this.startController), new DefaultWrapperAnimation());
        this.initialized = true;
        return this.groupWrapper.get().getWrapper();
    }

    @Override
    public void createSubGroup(Class<?> originController, Class<?> startController, String groupId, Consumer<Pane> readyConsumer) {
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
                this.resources, this.registry, readyConsumer, origin));
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
            AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), OnDestroy.class, OnDestroy::value);
            controller.destroy();
            if (this.loadedControllers.isEmpty()) {
                if (!ControllerRegistry.removeGroup(this.groupId)) {
                    System.out.println("ERROR: UNABLE TO REMOVE GROUP " + this.groupId);
                    return;
                }
                this.groupWrapper.unbind();
                this.visibility.unbind();
                this.activeController.unbind();
            }
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
    public void destroy() {
        this.loadedControllers.values().forEach(c -> c.getSubGroups().values().forEach(IControllerGroup::destroy));
        this.loadedControllers.keySet().forEach(this::destroy);
    }

    private IController createController(Class<?> clazz) {
        if (!this.startController.equals(clazz) && ControllerRegistry.isRegistered(clazz)) {
            throw new InvalidControllerGroupDefinitionException("Controller \"" + clazz.getSimpleName() + "\" is already registered in another group!");
        }
        final IController controller = this.creator.createController(clazz);
        ControllerRegistry.addController(this.groupId, clazz);
        this.loadedControllers.put(clazz, controller);
        controller.visibilityProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal.type().equals(newVal.type())) {
                return;
            }
            if (newVal.type().equals(VisibilityState.SHOWN)) {
                AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), OnShow.class, OnShow::value,
                        true, controller.getVisibilityContext());
            } else if (newVal.type().equals(VisibilityState.HIDDEN)) {
                AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), OnHide.class, OnHide::value,
                        true, controller.getVisibilityContext());
            }
        });
        final ControllerSetupContext ctx = new ControllerSetupContext(clazz, this, this.groupCtx);
        final Object instance = controller.getControllerInstance();
        AnnotationUtils.invokeMethodsByAnnotation(instance, Setup.class, Setup::value, true, ctx);
        controller.getSubGroups().values().forEach(IControllerGroup::start);
        FXThreadUtils.runOnFXThread(() -> AnnotationUtils.invokeMethodsByAnnotation(instance, PostConstruct.class, PostConstruct::value));
        return controller;
    }

    private void setController(IController controller, IWrapperAnimation animation) {
        if (controller.visibilityProperty().get().equals(VisibilityState.UNDEFINED)) {
            controller.visibilityProperty().addListener((obs, oldVal, newVal) -> controller.getSubGroups().values()
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
                    }));
            this.visibility.addListener((obs, oldVal, newVal) -> {
                if (activeController.get().equals(controller)) {
                    controller.visibilityProperty().set(newVal);
                }
            });
        }
        if (this.activeController.get() == null) {
            this.activeController.addListener((obs, oldVal, newVal) -> {
                if (this.visibility.get().equals(VisibilityState.UNDEFINED)) {
                    return;
                }
                if (oldVal != null) {
                    oldVal.visibilityProperty().set(VisibilityState.HIDDEN);
                }
                if (newVal != null) {
                    newVal.visibilityProperty().set(VisibilityState.SHOWN);
                }
            });
            this.activeController.set(controller);
            this.groupWrapper.get().setController(controller);
        } else {
            this.activeController.set(controller);
            this.groupWrapper.get().switchController(controller, animation);
        }
    }

    @Override
    public ObjectProperty<VisibilityState> visibilityProperty() {
        return this.visibility;
    }

}
