package de.intelligence.bachelorarbeit.reflectionutils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * The {@link InstanceReflection} class provides methods to perform reflective operations on instantiated objects.
 *
 * @author Deniz Groenhoff
 */
public final class InstanceReflection extends ReflectableScope<Object> implements ExceptionHandleable {

    private IReflectionExceptionHandler handler;

    InstanceReflection(Object instance) {
        super(instance);
        this.handler = IReflectionExceptionHandler.DEFAULT;
    }

    private void iterateMethods(Object accessor, @Nullable Identifier<MethodReflection> identifier,
                                Callback<MethodReflection> callback) {
        Arrays.stream(accessor.getClass().getDeclaredMethods())
                .map(m -> Reflection.setExceptionHandler(new MethodReflection(m, accessor), this.handler))
                .filter(m -> identifier != null && identifier.check(m)).forEach(callback::callback);
        if (accessor.getClass().isInterface()) {
            Arrays.stream(accessor.getClass().getInterfaces())
                    .forEach(m -> this.iterateMethods(accessor.getClass(), identifier, callback));
            return;
        }
        if (accessor.getClass().getSuperclass() != null && accessor.getClass().getSuperclass() != Object.class) {
            this.iterateMethods(accessor.getClass().getSuperclass(), identifier, callback);
        }
    }

    private void iterateFields(Object accessor, @Nullable Identifier<FieldReflection> identifier,
                               Callback<FieldReflection> callback) {
        Arrays.stream(accessor.getClass().getDeclaredFields())
                .map(f -> Reflection.setExceptionHandler(new FieldReflection(f, accessor), this.handler))
                .filter(f -> identifier != null && identifier.check(f)).forEach(callback::callback);
        if (accessor.getClass().getSuperclass() != null && accessor.getClass().getSuperclass() != Object.class) {
            this.iterateFields(accessor.getClass().getSuperclass(), identifier, callback);
        }
    }

    /**
     * Starts the reflection with a {@link Field} as the entry point
     *
     * @param field The {@link Field}
     * @return A {@link FieldReflection} instance representing the entry point
     */
    public FieldReflection reflectField(Field field) {
        return Reflection.reflect(field, super.reflectable);
    }

    /**
     * Starts the reflection with a {@link Field} as the entry point
     *
     * @param name The name of the {@link Field}
     * @return A {@link FieldReflection} instance representing the entry point
     */
    public FieldReflection reflectField(String name) {
        return Reflection.reflect(Reflection.handleReflectiveExceptions(this.handler,
                () -> super.reflectable.getClass().getDeclaredField(name)), super.reflectable);
    }

    /**
     * Starts the reflection with a {@link Method} as the entry point
     *
     * @param method The {@link Method}
     * @return A {@link MethodReflection} instance representing the entry point
     */
    public MethodReflection reflectMethod(Method method) {
        return Reflection.reflect(method, super.reflectable);
    }

    /**
     * Starts the reflection with a {@link Method} as the entry point
     *
     * @param name     The name of the wanted {@link Method}
     * @param argTypes The parameter types of the wanted {@link Method}
     * @return A {@link MethodReflection} instance representing the entry point
     * @throws IllegalArgumentException if no suitable method was found
     */
    public MethodReflection reflectMethod(String name, Class<?>... argTypes) {
        if (argTypes == null) {
            argTypes = new Class[0];
        }
        for (var method : super.reflectable.getClass().getDeclaredMethods()) {
            if (!method.getName().equals(name) || method.getParameterCount() != argTypes.length) {
                continue;
            }
            if (matchArguments(argTypes, method.getParameterTypes())) {
                return Reflection.setExceptionHandler(Reflection.reflect(method, super.reflectable), this.handler);
            }
        }
        throw new IllegalArgumentException("No suitable method found!");
    }

    /**
     * Iterates methods, converts them to a {@link MethodReflection}, filters them by an {@link Identifier}
     * and executes a {@link Callback} on each of them
     *
     * @param identifier The MethodReflection {@link Identifier}
     * @param callback   The MethodReflection {@link Callback}
     * @return This instance
     */
    public InstanceReflection iterateMethods(@Nullable Identifier<MethodReflection> identifier, Callback<MethodReflection> callback) {
        this.iterateMethods(super.reflectable, identifier, callback);
        return this;
    }

    /**
     * Iterates fields, converts them to an {@link FieldReflection}, filters them by an {@link Identifier}
     * and executes a {@link Callback} on each of them
     *
     * @param identifier The FieldReflection {@link Identifier}
     * @param callback   The FieldReflection {@link Callback}
     * @return This instance
     */
    public InstanceReflection iterateFields(@Nullable Identifier<FieldReflection> identifier, Callback<FieldReflection> callback) {
        this.iterateFields(super.reflectable, identifier, callback);
        return this;
    }

    /**
     * Returns the encapsulated object with an unsafe cast to the wanted type-inferred object
     *
     * @return The type-inferred object
     */
    public <T> T getReflectableUnsafe() {
        return Reflection.unsafeCast(super.reflectable);
    }

    @Override
    public void setExceptionHandler(IReflectionExceptionHandler handler) {
        this.handler = handler;
    }

}
