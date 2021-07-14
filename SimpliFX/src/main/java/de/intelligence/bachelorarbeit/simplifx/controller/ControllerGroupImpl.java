package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.exception.InvalidControllerGroupDefinitionException;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.utils.AnnotationUtils;
import de.intelligence.bachelorarbeit.simplifx.utils.FXThreadUtils;

public final class ControllerGroupImpl implements IControllerGroup {

    private final String groupId;
    private final Class<?> startController;
    private final IControllerFactoryProvider provider;
    private final II18N ii18N;
    private final Consumer<Pane> readyConsumer;

    private final ControllerCreator creator;
    private final Map<Class<?>, IController> loadedControllers;
    private final ObjectProperty<IControllerGroupWrapper> groupWrapper;
    private final ObjectProperty<IController> activeController;
    private final ControllerGroupContext groupCtx;
    private final ObjectProperty<VisibilityState> visibility;

    public ControllerGroupImpl(String groupId, Class<?> startController, IControllerFactoryProvider provider, II18N ii18N, Consumer<Pane> readyConsumer) {
        this.groupId = groupId;
        this.startController = startController;
        this.provider = provider;
        this.ii18N = ii18N;
        this.readyConsumer = readyConsumer;
        this.creator = new ControllerCreator(provider, ii18N);
        this.loadedControllers = new ConcurrentHashMap<>();
        this.groupWrapper = new SimpleObjectProperty<>(new ControllerGroupWrapperImpl());
        this.activeController = new SimpleObjectProperty<>();
        this.groupCtx = new ControllerGroupContext(this);
        this.visibility = new SimpleObjectProperty<>(VisibilityState.UNDEFINED);
        ControllerRegistry.register(groupId, this.groupCtx);
        ControllerRegistry.addController(groupId, startController);
    }

    @Override
    public IController constructController(Class<?> clazz, Consumer<Pane> readyConsumer) {
        if (!this.startController.equals(clazz) && ControllerRegistry.isRegistered(clazz)) {
            throw new InvalidControllerGroupDefinitionException("Controller \"" + clazz.getSimpleName() + "\" is already registered in another group!");
        }
        final IController controller = this.creator.createController(clazz);
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
        final ControllerSetupContext ctx = new ControllerSetupContext(controller.getControllerClass(), this, this.groupCtx);
        AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), Setup.class, true, ctx);
        controller.getSubGroups().forEach((groupId, group) -> {
            group.start(); //TODO dont start in pre init
        });
        FXThreadUtils.runOnFXThread(() -> {
            postConstruct(controller.getControllerInstance());
        });
        return controller;
    }

    @Override
    public Pane start() {
        readyConsumer.accept(this.groupWrapper.get().getWrapper());
        final IController controller = this.getOrCreateController(this.startController);
        setController(controller, new DefaultWrapperAnimationFactory());
        return this.groupWrapper.get().getWrapper();
    }

    @Override
    public void registerSubGroup(Class<?> originController, Class<?> startController, String groupId, Consumer<Pane> readyConsumer) {
        if (ControllerRegistry.isRegistered(startController)) {
            throw new InvalidControllerGroupDefinitionException("Controller \"" + startController.getSimpleName() + "\" is already registered in another group!");
        }
        if (ControllerRegistry.isRegistered(groupId)) {
            throw new InvalidControllerGroupDefinitionException("Group with id \"" + groupId + "\" is already registered!");
        }
        this.loadedControllers.get(originController).getSubGroups().put(groupId, new ControllerGroupImpl(groupId, startController, this.provider, this.ii18N, readyConsumer));
    }

    @Override
    public void switchController(Class<?> newController) {
        this.switchController(newController, new DefaultWrapperAnimationFactory());
    }

    @Override
    public void switchController(Class<?> newController, IWrapperAnimationFactory factory) {
        if (this.activeController.get() == null || this.activeController.get().getControllerClass().equals(newController)) {
            return;
        }
        setController(this.getOrCreateController(newController), factory);
    }

    //TODO remove group when last controller destroyed and remove root
    @Override
    public void destroy(Class<?> clazz) {
        if (this.loadedControllers.containsKey(clazz)) {
            de.intelligence.bachelorarbeit.simplifx.controller.ControllerRegistry.removeController(clazz);
            final IController controller = this.loadedControllers.remove(clazz);
            AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), OnDestroy.class, OnDestroy::value);
            controller.destroy();
        }
    }

    //TODO unbind everything
    @Override
    public void destroy() {
        this.loadedControllers.values().forEach(c -> c.getSubGroups().values().forEach(IControllerGroup::destroy));
        this.loadedControllers.keySet().forEach(this::destroy);
    }

    @Override
    public Class<?> getActiveController() {
        if (this.activeController == null) {
            return null;
        }
        return this.activeController.get().getControllerClass();
    }

    @Override
    public ControllerGroupContext getContextFor(String groupId) {
        return ControllerRegistry.getContextFor(groupId);
    }

    @Override
    public ObjectProperty<VisibilityState> visibilityProperty() {
        return this.visibility;
    }

    public void setController(IController controller, IWrapperAnimationFactory factory) {
        if (controller.visibilityProperty().get().equals(VisibilityState.UNDEFINED)) {
            controller.getSubGroups().values().forEach(group ->
                    group.visibilityProperty().bind(Bindings.createObjectBinding(() -> {
                        final VisibilityState controllerState = controller.visibilityProperty().get();
                        if (controllerState.type().equals(VisibilityState.HIDDEN)) {
                            return VisibilityState.GROUP_HIDDEN;
                        } else if (controllerState.type().equals(VisibilityState.SHOWN)) {
                            return VisibilityState.GROUP_SHOWN;
                        }
                        return VisibilityState.UNDEFINED;
                    }, controller.visibilityProperty())));
            this.visibility.addListener((obs, oldVal, newVal) -> {
                if (activeController.get().equals(controller)) {
                    controller.visibilityProperty().set(newVal);
                }
            });
        }
        if (this.activeController.get() == null) {
            this.activeController.addListener((obs, oldVal, newVal) -> {
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
            this.groupWrapper.get().switchController(controller, factory);
        }
    }

    private void postConstruct(Object instance) {
        AnnotationUtils.invokeMethodsByAnnotation(instance, PostConstruct.class, PostConstruct::value);
    }

    private IController getOrCreateController(Class<?> clazz) {
        if (this.loadedControllers.containsKey(clazz)) {
            return this.loadedControllers.get(clazz);
        }
        return this.constructController(clazz, null);
    }

}
