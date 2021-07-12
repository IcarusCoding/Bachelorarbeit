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
        ControllerRegistry.addController(super.groupId, clazz);
        super.registeredControllers.put(clazz, created);
        return created;
    }

    @Override
    public Pane start(IControllerGroupWrapper wrapper) {
        Conditions.checkNull(wrapper, "wrapper must not be null.");
        super.groupWrapper.set(wrapper);
        final IController controller = this.getOrCreateController(super.startController);
        super.activeHandler.set(controller);
        wrapper.setController(controller);
        for (final IControllerGroup group : super.subGroups.values()) {
            group.start(new ControllerGroupWrapperImpl());
        }
        FXThreadUtils.runOnFXThread(() -> {
            super.readyConsumer.accept(wrapper.getWrapper());
            AnnotationUtils.invokeMethodsByPrioritizedAnnotation(controller.getControllerInstance(),
                    PostConstruct.class, m -> m.getParameterCount() == 0, PostConstruct::value);
        });
        return wrapper.getWrapper();
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

    //TODO unbind everything
    @Override
    public void destroy() {
        super.subGroups.values().forEach(IControllerGroup::destroy);
        super.registeredControllers.keySet().forEach(this::destroy);
    }

    @Override
    public void registerSubGroup(Class<?> startController, String groupId, Consumer<Pane> readyConsumer) {
        if (ControllerRegistry.isRegistered(groupId)) {
            throw new InvalidControllerGroupDefinitionException("Group with id \"" + groupId + "\" is already registered!");
        }
        if (ControllerRegistry.isRegistered(startController)) {
            throw new InvalidControllerGroupDefinitionException("Controller \"" + startController.getSimpleName() + "\" is already registered in another group!");
        }
        super.subGroups.put(groupId, new ControllerGroupImpl(startController, super.provider, super.ii18N, readyConsumer, groupId));
    }

    @Override
    public void switchController(Class<?> newController, IWrapperAnimationFactory factory) {
        if (super.activeHandler.get() == null || super.activeHandler.get().getControllerClass().equals(newController)) {
            return;
        }
        final IController controller = this.getOrCreateController(newController);
        super.groupWrapper.get().switchController(controller, factory);
        super.activeHandler.set(controller);
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
