package de.intelligence.bachelorarbeit.reflectionutils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * The {@link MethodReflection} class provides methods to perform reflective operations on {@link Method} level.
 *
 * @author Deniz Groenhoff
 */
public final class MethodReflection extends ReflectableMember<Method> {

    MethodReflection(Method method) {
        super(method);
    }

    MethodReflection(Method method, Object accessor) {
        super(method, accessor);
    }

    /**
     * Invokes the {@link Method}
     *
     * @param args The {@link Method} parameters
     * @return The type-inferred return value of the invoked {@link Method}
     */
    public <E> E invokeUnsafe(Object... args) {
        return Reflection.unsafeCast(invoke(args));
    }

    /**
     * Invokes the {@link Method}
     *
     * @param args The {@link Method} parameters
     * @return The return value of the invoked {@link Method} as an {@link Object}
     */
    public Object invoke(Object... args) {
        return Reflection.makeAccessibleAndExecute(super.reflectable,
                Modifier.isStatic(super.reflectable.getModifiers()) ? null : super.accessor, super.shouldForceAccess,
                super.handler, () -> super.reflectable.invoke(super.accessor, args));
    }

    @Override
    public MethodReflection forceAccess() {
        super.shouldForceAccess = true;
        return this;
    }

}
