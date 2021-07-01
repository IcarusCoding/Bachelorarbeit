package de.intelligence.bachelorarbeit.reflectionutils;

import java.lang.reflect.Constructor;

/**
 * The {@link ConstructorReflection} class provides methods to perform reflective operations on {@link Constructor} level.
 *
 * @author Deniz Groenhoff
 */
public final class ConstructorReflection extends ReflectableMember<Constructor<?>> {

    ConstructorReflection(Constructor<?> constructor) {
        super(constructor);
    }

    /**
     * Instantiates a class and returns a new {@link InstanceReflection} containing the instantiated object
     *
     * @param args The constructor parameters
     * @return The instantiated object encapsulated in an {@link InstanceReflection}
     */
    public InstanceReflection instantiate(Object... args) {
        return Reflection.setExceptionHandler(new InstanceReflection(instantiateAndGet(args)), super.handler);
    }

    /**
     * Instantiates a class and returns the proper instantiated object by type inference
     *
     * @param args The constructor parameters
     * @return The type inferred, instantiated object
     */
    public <T> T instantiateUnsafeAndGet(Object... args) {
        return Reflection.unsafeCast(instantiateAndGet(args));
    }

    /**
     * Instantiates a class and returns instantiated object
     *
     * @param args The constructor parameters
     * @return The instantiated object
     */
    public Object instantiateAndGet(Object... args) {
        return Reflection.makeAccessibleAndExecute(super.reflectable, null, super.shouldForceAccess, super.handler,
                () -> super.reflectable.newInstance(args));
    }

    @Override
    public ConstructorReflection forceAccess() {
        super.shouldForceAccess = true;
        return this;
    }

}
