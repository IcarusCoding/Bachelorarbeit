package de.intelligence.bachelorarbeit.simplifx.realC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.utils.AnnotationUtils;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;
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
        this.groupWrapper = new SimpleObjectProperty<>();
        this.activeController = new SimpleObjectProperty<>();
        this.groupCtx = new ControllerGroupContext(this);
        this.visibility = new SimpleObjectProperty<>(VisibilityState.UNDEFINED);
        ControllerRegistry.register(groupId, this.groupCtx);
        ControllerRegistry.addController(groupId, startController);
    }

    @Override
    public IController constructController(Class<?> clazz) {
        if (!this.startController.equals(clazz) && ControllerRegistry.isRegistered(clazz)) {
            throw new RuntimeException("RUNTIME EXCEPTION");
        }
        final IController controller = this.creator.createController(clazz);
        this.loadedControllers.put(clazz, controller);
        controller.visibilityProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("NEW STATE FOR " + controller.getControllerClass().getSimpleName() + " -> " + newVal);
            if (newVal.type().equals(VisibilityState.SHOWN)) {
                //AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), OnShow.class);
            } else if (newVal.type().equals(VisibilityState.HIDDEN)) {
            }
        });
        // Setup
        final ControllerSetupContext ctx = new ControllerSetupContext(controller.getControllerClass(), this, this.groupCtx);
        AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), Setup.class, true, false, ctx);
        controller.getSubGroups().forEach((groupId, group) -> {
            group.visibilityProperty().bind(controller.visibilityProperty());
            group.start(new ControllerGroupWrapperImpl()); //TODO dont start in pre init
        });
        return controller;
    }

    @Override
    public Pane start(IControllerGroupWrapper wrapper) {
        Conditions.checkNull(wrapper, "wrapper must not be null.");
        this.groupWrapper.set(wrapper);
        final IController controller = this.getOrCreateController(this.startController);
        setController(controller, new DefaultWrapperAnimationFactory());
        initController(controller, this.readyConsumer);
        return wrapper.getWrapper();
    }

    @Override
    public void registerSubGroup(Class<?> originController, Class<?> startController, String groupId, Consumer<Pane> readyConsumer) {
        if (ControllerRegistry.isRegistered(startController)) {
            throw new RuntimeException("RUNTIME EXCEPTION");
        }
        if (ControllerRegistry.isRegistered(groupId)) {
            throw new RuntimeException("RUNTIME EXCEPTION");
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
        boolean needsPostConstruct = !this.loadedControllers.containsKey(newController); //TODO ugly
        final IController controller = this.getOrCreateController(newController);
        if (needsPostConstruct) {
            postConstruct(controller.getControllerInstance());
        }
        setController(controller, factory);
    }

    //TODO remove group when last controller destroyed and remove root
    @Override
    public void destroy(Class<?> clazz) {
        if (this.loadedControllers.containsKey(clazz)) {
            de.intelligence.bachelorarbeit.simplifx.controller.ControllerRegistry.removeController(clazz);
            final IController controller = this.loadedControllers.remove(clazz);
            AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), OnDestroy.class, OnDestroy::value, true);
            controller.destroy();
        }
    }

    @Override
    public ObjectProperty<VisibilityState> visibilityProperty() {
        return this.visibility;
    }

    //TODO unbind everything
    @Override
    public void destroy() {
        this.loadedControllers.values().forEach(c -> c.getSubGroups().values().forEach(IControllerGroup::destroy));
        this.loadedControllers.keySet().forEach(this::destroy);
    }

    @Override
    public ControllerGroupContext getContextFor(String groupId) {
        return ControllerRegistry.getContextFor(groupId);
    }

    @Override
    public Class<?> getActiveController() {
        if (this.activeController == null) {
            return null;
        }
        return this.activeController.get().getControllerClass();
    }

    public void setController(IController controller, IWrapperAnimationFactory factory) {
        if (controller.visibilityProperty().get().equals(VisibilityState.UNDEFINED)) {
            controller.getSubGroups().values().forEach(group -> {
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
            this.visibility.addListener((obs, oldVal, newVal) -> {
                if (activeController.get().equals(controller)) {
                    controller.visibilityProperty().set(newVal);
                }
            });
        }
        if (this.activeController.get() == null) {
            this.activeController.set(controller);
            this.activeController.addListener((obs, oldVal, newVal) -> {
                if (oldVal != null) {
                    oldVal.visibilityProperty().set(VisibilityState.HIDDEN);
                }
                if (newVal != null) {
                    newVal.visibilityProperty().set(VisibilityState.SHOWN);
                }
            });
            this.groupWrapper.get().setController(controller);
        } else {
            this.activeController.set(controller);
            this.groupWrapper.get().switchController(controller, factory, state -> {
            });
        }
    }

    private void initController(IController controller, Consumer<Pane> readyConsumer) {
        FXThreadUtils.runOnFXThread(() -> {
            readyConsumer.accept(this.groupWrapper.get().getWrapper());
            postConstruct(controller.getControllerInstance());
        });
    }

    private void postConstruct(Object instance) {
        // PostConstruct
        AnnotationUtils.invokeMethodsByAnnotation(instance, PostConstruct.class, PostConstruct::value, true);
    }

    private IController getOrCreateController(Class<?> clazz) {
        if (this.loadedControllers.containsKey(clazz)) {
            return this.loadedControllers.get(clazz);
        }
        return this.constructController(clazz);
    }

}
