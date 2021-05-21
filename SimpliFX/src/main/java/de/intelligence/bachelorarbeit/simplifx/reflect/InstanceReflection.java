package de.intelligence.bachelorarbeit.simplifx.reflect;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import de.intelligence.bachelorarbeit.simplifx.reflect.utils.Callback;
import de.intelligence.bachelorarbeit.simplifx.reflect.utils.Identifier;

public final class InstanceReflection extends ReflectableType<Object> {

    InstanceReflection(Object instance) {
        super(instance);
    }

    private static void iterateMethods(Object accessor, @Nullable Identifier<Method> identifier, Callback<MethodReflection> callback) {
        Arrays.stream(accessor.getClass().getDeclaredMethods()).filter(m -> identifier != null && identifier.check(m))
                .forEach(m -> callback.callback(new MethodReflection(m, accessor)));
        if (accessor.getClass().isInterface()) {
            Arrays.stream(accessor.getClass().getInterfaces()).forEach(m -> iterateMethods(accessor.getClass(), identifier, callback));
            return;
        }
        if (accessor.getClass().getSuperclass() != null && accessor.getClass().getSuperclass() != Object.class) {
            iterateMethods(accessor.getClass().getSuperclass(), identifier, callback);
        }
    }

    private static void iterateFields(Object accessor, @Nullable Identifier<Field> identifier, Callback<FieldReflection> callback) {
        Arrays.stream(accessor.getClass().getDeclaredFields()).filter(f -> identifier != null && identifier.check(f))
                .forEach(f -> callback.callback(new FieldReflection(f, accessor)));
        if (accessor.getClass().getSuperclass() != null && accessor.getClass().getSuperclass() != Object.class) {
            InstanceReflection.iterateFields(accessor.getClass().getSuperclass(), identifier, callback);
        }
    }

    public FieldReflection reflectField(Field field) {
        return Reflection.reflect(field, super.reflectable);
    }

    public FieldReflection reflectField(String name) {
        return Reflection.reflect(Reflection.handleReflectiveExceptions(() -> super.reflectable.getClass().getDeclaredField(name)), super.reflectable);
    }

    public MethodReflection reflectMethod(Method method) {
        return Reflection.reflect(method, super.reflectable);
    }

    public MethodReflection reflectMethod(String name, Class<?>... argTypes) {
        if (argTypes == null) {
            argTypes = new Class[0];
        }
        for (var method : super.reflectable.getClass().getDeclaredMethods()) {
            if (!method.getName().equals(name) || method.getParameterCount() != argTypes.length) {
                continue;
            }
            if (matchArguments(argTypes, method.getParameterTypes())) {
                return Reflection.reflect(method, super.reflectable);
            }
        }
        throw new IllegalArgumentException("No suitable method found!");
    }

    public InstanceReflection iterateMethods(@Nullable Identifier<Method> identifier, Callback<MethodReflection> callback) {
        InstanceReflection.iterateMethods(super.reflectable, identifier, callback);
        return this;
    }

    public InstanceReflection iterateFields(@Nullable Identifier<Field> identifier, Callback<FieldReflection> callback) {
        InstanceReflection.iterateFields(super.reflectable, identifier, callback);
        return this;
    }

    public <T> T getReflectableUnsafe() {
        return Reflection.unsafeCast(super.reflectable);
    }

}
