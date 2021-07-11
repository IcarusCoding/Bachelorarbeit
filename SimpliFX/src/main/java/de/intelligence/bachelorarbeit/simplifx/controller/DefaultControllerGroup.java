package de.intelligence.bachelorarbeit.simplifx.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.utils.AnnotationUtils;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

public final class DefaultControllerGroup extends AbstractControllerGroup {

    public DefaultControllerGroup(Class<?> startController, IControllerFactoryProvider controllerProvider, II18N ii18N) {
        super(startController, new ControllerCreator(controllerProvider, ii18N));
    }

    private IController getOrCreateController(Class<?> clazz) throws IOException {
        final Optional<IController> conOpt = registeredControllers.entrySet().stream().filter(e -> e.getKey().equals(clazz))
                .map(Map.Entry::getValue).findFirst();
        if (conOpt.isPresent()) {
            return conOpt.get();
        }
        final IController created = super.creator.createController(clazz);
        super.registeredControllers.put(clazz, created);
        return created;
    }

    @Override
    public Pane start(IControllerGroupWrapper wrapper) throws IOException {
        Conditions.checkNull(wrapper, "wrapper must not be null.");
        super.groupWrapper.set(wrapper);
        wrapper.setController(this.getOrCreateController(super.startController));
        return wrapper.getWrapper();
    }

    @Override
    public void destroy(Class<?> clazz) {
        if (super.registeredControllers.containsKey(clazz)) {
            final IController controller = super.registeredControllers.remove(clazz);
            AnnotationUtils.invokeMethodsByPrioritizedAnnotation(controller.getControllerInstance(), OnDestroy.class,
                    m -> m.getParameterCount() == 0, OnDestroy::value);
            controller.destroy();
        }
    }

    @Override
    public void destroy() {
        super.registeredControllers.keySet().forEach(this::destroy);
    }

}
