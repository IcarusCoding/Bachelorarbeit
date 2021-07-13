package de.intelligence.bachelorarbeit.simplifx.realC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.controller.OnHide;
import de.intelligence.bachelorarbeit.simplifx.controller.OnShow;
import de.intelligence.bachelorarbeit.simplifx.controller.Setup;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.utils.AnnotationUtils;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;
import de.intelligence.bachelorarbeit.simplifx.utils.FXThreadUtils;

public final class ControllerGroupImpl implements IControllerGroup {

    // constructController() -> Creates a controller with a ControllerCreator

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
        // Setup
        final ControllerSetupContext ctx = new ControllerSetupContext(controller.getControllerClass(), this, this.groupCtx);
        AnnotationUtils.invokeMethodsByAnnotation(controller.getControllerInstance(), Setup.class,
                m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(ControllerSetupContext.class), ctx);
        controller.getSubGroups().forEach((groupId, group) -> {
            group.start(new ControllerGroupWrapperImpl());
        });
        return controller;
    }

    @Override
    public Pane start(IControllerGroupWrapper wrapper) {
        Conditions.checkNull(wrapper, "wrapper must not be null.");
        this.groupWrapper.set(wrapper);
        final IController controller = this.getOrCreateController(this.startController);
        this.activeController.set(controller);
        this.activeController.addListener((obs, oldVal, newVal) -> {
            if (oldVal != null) {
                AnnotationUtils.invokeMethodsByPrioritizedAnnotation(oldVal.getControllerInstance(), OnHide.class,
                        m -> m.getParameterCount() == 0, OnHide::value);
            }
            if (newVal != null) {
                AnnotationUtils.invokeMethodsByPrioritizedAnnotation(newVal.getControllerInstance(), OnShow.class,
                        m -> m.getParameterCount() == 0, OnShow::value);
            }
        });
        wrapper.setController(controller);
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
        this.groupWrapper.get().switchController(controller, factory, state -> {
        });
        this.activeController.set(controller);
    }

    //TODO remove group when last controller destroyed and remove root
    @Override
    public void destroy(Class<?> clazz) {
        if (this.loadedControllers.containsKey(clazz)) {
            de.intelligence.bachelorarbeit.simplifx.controller.ControllerRegistry.removeController(clazz);
            final IController controller = this.loadedControllers.remove(clazz);
            AnnotationUtils.invokeMethodsByPrioritizedAnnotation(controller.getControllerInstance(), OnDestroy.class,
                    m -> m.getParameterCount() == 0, OnDestroy::value);
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

    private void initController(IController controller, Consumer<Pane> readyConsumer) {
        FXThreadUtils.runOnFXThread(() -> {
            readyConsumer.accept(this.groupWrapper.get().getWrapper());
            postConstruct(controller.getControllerInstance());
        });
    }

    private void postConstruct(Object instance) {
        // PostConstruct
        AnnotationUtils.invokeMethodsByPrioritizedAnnotation(instance, PostConstruct.class,
                m -> m.getParameterCount() == 0, PostConstruct::value);
    }

    private IController getOrCreateController(Class<?> clazz) {
        if (this.loadedControllers.containsKey(clazz)) {
            return this.loadedControllers.get(clazz);
        }
        return this.constructController(clazz);
    }

}
