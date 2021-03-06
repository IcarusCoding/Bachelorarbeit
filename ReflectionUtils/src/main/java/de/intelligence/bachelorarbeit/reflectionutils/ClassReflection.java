package de.intelligence.bachelorarbeit.reflectionutils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;

/**
 * The {@link ClassReflection} class provides methods to perform reflective operations on {@link Class} level.
 *
 * @author Deniz Groenhoff
 */
public final class ClassReflection extends ReflectableScope<Class<?>> implements Annotatable, ExceptionHandleable {

    private IReflectionExceptionHandler handler;

    ClassReflection(Class<?> clazz) {
        super(clazz);
    }

    private static void iterateMethods(Class<?> clazz, @Nullable Identifier<Method> identifier,
                                       Callback<Method> callback) {
        Arrays.stream(clazz.getDeclaredMethods()).filter(m -> identifier != null && identifier.check(m))
                .forEach(callback::callback);
        if (clazz.isInterface()) {
            Arrays.stream(clazz.getInterfaces()).forEach(c -> iterateMethods(c, identifier, callback));
            return;
        }
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            ClassReflection.iterateMethods(clazz.getSuperclass(), identifier, callback);
        }
    }

    private static void iterateConstructors(Class<?> clazz, @Nullable Identifier<Constructor<?>> identifier,
                                            Callback<Constructor<?>> callback) {
        Arrays.stream(clazz.getDeclaredConstructors()).filter(c -> identifier != null && identifier.check(c))
                .forEach(callback::callback);
    }

    private static void iterateFields(Class<?> clazz, @Nullable Identifier<Field> identifier,
                                      Callback<Field> callback) {
        Arrays.stream(clazz.getDeclaredFields()).filter(f -> identifier != null && identifier.check(f))
                .forEach(callback::callback);
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            ClassReflection.iterateFields(clazz.getSuperclass(), identifier, callback);
        }
    }

    public boolean isNonStaticMember() {
        return super.reflectable.isMemberClass() && !Modifier.isStatic(super.reflectable.getModifiers());
    }

    /**
     * Finds a constructor with the specified parameter types
     *
     * @param argTypes The constructor parameter types
     * @return A {@link ConstructorReflection} instance representing the new entry point
     */
    public ConstructorReflection findConstructor(Class<?>... argTypes) {
        final ConstructorReflection constructorRef = this.findConstructor0(argTypes);
        if (constructorRef != null) {
            return constructorRef;
        }
        throw new IllegalArgumentException("No suitable constructor found!");
    }

    /**
     * Finds a constructor with the specified parameter types
     *
     * @param argTypes The constructor parameter types
     * @return An {@link Optional} containing a {@link ConstructorReflection} instance representing the new entry point
     * or {@link Optional#empty()} if no constructor was found
     */
    public Optional<ConstructorReflection> hasConstructor(Class<?>... argTypes) {
        return Optional.ofNullable(this.findConstructor0(argTypes));
    }

    private ConstructorReflection findConstructor0(Class<?>... argTypes) {
        if (argTypes == null) {
            argTypes = new Class[0];
        }
        for (var constructor : super.reflectable.getDeclaredConstructors()) {
            if (constructor.getParameterCount() != argTypes.length) {
                continue;
            }
            if (Reflection.matchArguments(argTypes, constructor.getParameterTypes())) {
                return Reflection.setExceptionHandler(Reflection.reflect(constructor), this.handler);
            }
        }
        return null;
    }

    /**
     * Iterates methods, filters them by an {@link Identifier} and executes a {@link Callback} on each of them
     *
     * @param identifier The method {@link Identifier}
     * @param callback   The method {@link Callback}
     * @return This instance
     */
    public ClassReflection iterateMethods(@Nullable Identifier<Method> identifier, Callback<Method> callback) {
        ClassReflection.iterateMethods(super.reflectable, identifier, callback);
        return this;
    }

    /**
     * Iterates constructors, filters them by an {@link Identifier} and executes a {@link Callback} on each of them
     *
     * @param identifier The constructor {@link Identifier}
     * @param callback   The constructor {@link Callback}
     * @return This instance
     */
    public ClassReflection iterateConstructors(@Nullable Identifier<Constructor<?>> identifier,
                                               Callback<Constructor<?>> callback) {
        ClassReflection.iterateConstructors(super.reflectable, identifier, callback);
        return this;
    }

    /**
     * Iterates fields, filters them by an {@link Identifier} and executes a {@link Callback} on each of them
     *
     * @param identifier The field {@link Identifier}
     * @param callback   The field {@link Callback}
     * @return This instance
     */
    public ClassReflection iterateFields(@Nullable Identifier<Field> identifier, Callback<Field> callback) {
        ClassReflection.iterateFields(super.reflectable, identifier, callback);
        return this;
    }

    /**
     * Starts the reflection with a static {@link Method} as the entry point
     *
     * @param name     The name of the wanted {@link Method}
     * @param argTypes The parameter types of the wanted {@link Method}
     * @return A {@link MethodReflection} instance representing the entry point
     * @throws IllegalArgumentException if no suitable method was found
     */
    public MethodReflection reflectMethod(String name, Class<?>... argTypes) {
        final MethodReflection methodRef = this.reflectMethod0(name, argTypes);
        if (methodRef != null) {
            return methodRef;
        }
        throw new IllegalArgumentException("No suitable method found!");
    }

    public Optional<MethodReflection> hasMethod(String name, Class<?>... argTypes) {
        return Optional.ofNullable(this.reflectMethod0(name, argTypes));
    }

    private MethodReflection reflectMethod0(String name, Class<?>... argTypes) {
        if (argTypes == null) {
            argTypes = new Class[0];
        }
        for (var method : super.reflectable.getDeclaredMethods()) {
            if (!method.getName().equals(name) || method.getParameterCount() != argTypes.length) {
                continue;
            }
            if (Reflection.matchArguments(argTypes, method.getParameterTypes())) {
                return Reflection.setExceptionHandler(Reflection.reflect(method, null), this.handler);
            }
        }
        return null;
    }

    public FieldReflection reflectField(String name) {
        for (var field : super.reflectable.getDeclaredFields()) {
            if (!field.getName().equals(name)) {
                continue;
            }
            return Reflection.setExceptionHandler(new FieldReflection(field, null), this.handler);
        }
        throw new IllegalArgumentException("No suitable field found!");
    }

    public boolean canAccept(Object obj) {
        return this.canAccept(obj.getClass());
    }

    public boolean canAccept(Class<?> clazz) {
        return super.reflectable.isAssignableFrom(clazz);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return super.reflectable.isAnnotationPresent(annotation);
    }

    @Override
    public <S extends Annotation> Optional<S> getAnnotation(Class<S> annotation) {
        return Optional.ofNullable(super.reflectable.getDeclaredAnnotation(annotation));
    }

    @Override
    public AnnotatedElement getAnnotatableElement() {
        return super.reflectable;
    }

    @Override
    public void setExceptionHandler(IReflectionExceptionHandler handler) {
        if (handler != null) {
            this.handler = handler;
        }
    }

}
