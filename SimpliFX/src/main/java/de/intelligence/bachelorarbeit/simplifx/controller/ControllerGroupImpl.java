package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.exception.InvalidControllerGroupDefinitionException;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.utils.AnnotationUtils;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;
import de.intelligence.bachelorarbeit.simplifx.utils.FXThreadUtils;

public final class ControllerGroupImpl extends AbstractControllerGroup {

    public ControllerGroupImpl(Class<?> startController, IControllerFactoryProvider controllerProvider, II18N ii18N, Consumer<Pane> readyConsumer, String groupId) {
        super(startController, controllerProvider, ii18N, readyConsumer, groupId);
    }

    private IController getOrCreateController(Class<?> clazz) {
        final Optional<IController> conOpt = registeredControllers.entrySet().stream().filter(e -> e.getKey().equals(clazz))
                .map(Map.Entry::getValue).findFirst();
        if (conOpt.isPresent()) {
            return conOpt.get();
        }
        if (!super.startController.equals(clazz) && ControllerRegistry.isRegistered(clazz)) {
            //TODO handle
            throw new InvalidControllerGroupDefinitionException("Controller \"" + clazz.getSimpleName() + "\" is already registered in another group!");
        }
        final IController created = super.creator.createController(clazz);
        created.getVisibilityContext().stateProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("NEW STATE " + created.getControllerClass().getSimpleName() + " -> " + newVal);
            /*if(ControllerVisibilityContext.VisibilityState.SHOWN.equals(newVal)) {
                AnnotationUtils.invokeMethodsByPrioritizedAnnotation(created.getControllerInstance(), OnShow.class,
                        m -> m.getParameterCount() == 0, OnShow::value);
            } else if(ControllerVisibilityContext.VisibilityState.HIDDEN.equals(newVal)) {
                AnnotationUtils.invokeMethodsByPrioritizedAnnotation(created.getControllerInstance(), OnHide.class,
                        m -> m.getParameterCount() == 0, OnHide::value);
            }*/
        });
        ControllerRegistry.addController(super.groupId, clazz);
        super.registeredControllers.put(clazz, created);
        return created;
    }

    @Override
    public Pane start(IControllerGroupWrapper wrapper) {
        Conditions.checkNull(wrapper, "wrapper must not be null.");
        super.groupWrapper.set(wrapper); // shown 100%
        final IController controller = this.getOrCreateController(super.startController);
        super.activeHandler.set(controller);
        super.activeHandler.addListener((obs, oldVal, newVal) -> {
            if (oldVal != null) {
                oldVal.getVisibilityContext().setState(ControllerVisibilityContext.VisibilityState.HIDDEN);
            }
            if (newVal != null) {
                newVal.getVisibilityContext().setState(ControllerVisibilityContext.VisibilityState.SHOWN);
            }
        });
        wrapper.setController(controller);
        setupSubGroups(controller);
        FXThreadUtils.runOnFXThread(() -> {
            super.readyConsumer.accept(wrapper.getWrapper());
            AnnotationUtils.invokeMethodsByPrioritizedAnnotation(controller.getControllerInstance(),
                    PostConstruct.class, m -> m.getParameterCount() == 0, PostConstruct::value);
        });
        if (this.parent == null) {
            this.visibility.set(ControllerVisibilityContext.VisibilityState.SHOWN);
        }
        return wrapper.getWrapper();
    }

    //TODO unbind everything
    @Override
    public void destroy() {
        super.registeredControllers.values().forEach(c -> c.getSubGroups().values().forEach(IControllerGroup::destroy));
        super.registeredControllers.keySet().forEach(this::destroy);
    }

    //TODO remove group when last controller destroyed and remove root
    @Override
    public void destroy(Class<?> clazz) {
        if (super.registeredControllers.containsKey(clazz)) {
            ControllerRegistry.removeController(clazz);
            final IController controller = super.registeredControllers.remove(clazz);
            AnnotationUtils.invokeMethodsByPrioritizedAnnotation(controller.getControllerInstance(), OnDestroy.class,
                    m -> m.getParameterCount() == 0, OnDestroy::value);
            controller.destroy();
        }
    }

    @Override //TODO state check (only allow registering when not started)
    public void registerSubGroup(Class<?> startController, String groupId, Consumer<Pane> readyConsumer) {
        if (ControllerRegistry.isRegistered(groupId)) {
            throw new InvalidControllerGroupDefinitionException("Group with id \"" + groupId + "\" is already registered!");
        }
        if (ControllerRegistry.isRegistered(startController)) {
            throw new InvalidControllerGroupDefinitionException("Controller \"" + startController.getSimpleName() + "\" is already registered in another group!");
        }
        super.currentSubGroups.put(groupId,
                new ControllerGroupImpl(startController, super.provider, super.ii18N, readyConsumer, groupId));
    }

    @Override
    public void switchController(Class<?> newController, IWrapperAnimationFactory factory) {
        if (super.activeHandler.get() == null || super.activeHandler.get().getControllerClass().equals(newController)) {
            return;
        }
        final IController controller = this.getOrCreateController(newController);

        super.groupWrapper.get().switchController(controller, factory, state -> {
        });
        super.activeHandler.set(controller);
    }

    private void setupSubGroups(IController controller) {
        controller.getSubGroups().putAll(super.currentSubGroups);
        super.currentSubGroups.clear();
        for (final IControllerGroup group : controller.getSubGroups().values()) {
            group.setParent(controller);
            group.start(new ControllerGroupWrapperImpl());
        }
    }

    @Override
    public void switchController(Class<?> newController) {
        this.switchController(newController, new DefaultWrapperAnimationFactory());
    }

    @Override
    public ControllerGroupContext getContextFor(String groupId) {
        return ControllerRegistry.getContextFor(groupId);
    }

}
