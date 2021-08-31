package de.intelligence.bachelorarbeit.simplifx.experimental;

import java.util.HashMap;
import java.util.Map;

import javafx.util.Callback;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;

/**
 * A {@link IControllerFactoryProvider} which dynamically creates a subclass of the controller class at runtime.
 */
public final class SubclassControllerFactoryProvider implements IControllerFactoryProvider {

    private static final Map<Class<?>, Class<?>> SUBCLASS_REGISTRY = new HashMap<>();

    private final SubclassingClassLoader classLoader;
    private final IControllerFactoryProvider parent;

    /**
     * Instantiates a new SubclassControllerFactoryProvider instance.
     *
     * @param classLoader The {@link SubclassingClassLoader} that should be used to create subclasses.
     * @param parent      The {@link IControllerFactoryProvider} which will get called after the creation of the subclass.
     */
    public SubclassControllerFactoryProvider(SubclassingClassLoader classLoader, IControllerFactoryProvider parent) {
        this.classLoader = classLoader;
        this.parent = parent;
    }

    @Override
    public Callback<Class<?>, Object> provide() {
        return this::create;
    }

    @Override
    public Object create(Class<?> clazz) {
        final Object instance = Reflection.reflect(SUBCLASS_REGISTRY.computeIfAbsent(clazz, this.classLoader::defineSubclass))
                .findConstructor().instantiate().getReflectable();
        if (this.parent != null) {
            this.parent.handle(instance);
        }
        return instance;
    }

}
