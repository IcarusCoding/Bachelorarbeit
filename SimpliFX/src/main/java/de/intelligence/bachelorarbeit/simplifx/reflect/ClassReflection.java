package de.intelligence.bachelorarbeit.simplifx.reflect;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import de.intelligence.bachelorarbeit.simplifx.reflect.utils.Callback;
import de.intelligence.bachelorarbeit.simplifx.reflect.utils.Identifier;

public final class ClassReflection extends ReflectableType<Class<?>> {

    ClassReflection(Class<?> clazz) {
        super(clazz);
    }

    private static void iterateMethods(Class<?> clazz, @Nullable Identifier<Method> identifier, Callback<Method> callback) {
        Arrays.stream(clazz.getDeclaredMethods()).filter(m -> identifier != null && identifier.check(m))
                .forEach(callback::callback);
        if (clazz.isInterface()) {
            Arrays.stream(clazz.getInterfaces()).forEach(m -> iterateMethods(clazz, identifier, callback));
            return;
        }
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            ClassReflection.iterateMethods(clazz.getSuperclass(), identifier, callback);
        }
    }

    private static void iterateFields(Class<?> clazz, @Nullable Identifier<Field> identifier, Callback<Field> callback) {
        Arrays.stream(clazz.getDeclaredFields()).filter(f -> identifier != null && identifier.check(f))
                .forEach(callback::callback);
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            ClassReflection.iterateFields(clazz.getSuperclass(), identifier, callback);
        }
    }

    public ConstructorReflection findConstructor(Class<?>... argTypes) {
        if (argTypes == null) {
            argTypes = new Class[0];
        }
        for (var constructor : super.reflectable.getDeclaredConstructors()) {
            if (constructor.getParameterCount() != argTypes.length) {
                continue;
            }
            if (matchArguments(argTypes, constructor.getParameterTypes())) {
                return Reflection.reflect(constructor);
            }
        }
        throw new IllegalArgumentException("No suitable constructor found!");
    }

    public ClassReflection iterateMethods(@Nullable Identifier<Method> identifier, Callback<Method> callback) {
        ClassReflection.iterateMethods(super.reflectable, identifier, callback);
        return this;
    }

    public ClassReflection iterateFields(@Nullable Identifier<Field> identifier, Callback<Field> callback) {
        ClassReflection.iterateFields(super.reflectable, identifier, callback);
        return this;
    }

}
