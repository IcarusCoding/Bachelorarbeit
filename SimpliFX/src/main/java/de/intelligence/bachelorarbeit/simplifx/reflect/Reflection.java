package de.intelligence.bachelorarbeit.simplifx.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.function.BiPredicate;

import de.intelligence.bachelorarbeit.simplifx.reflect.utils.ExceptionRunnable;
import de.intelligence.bachelorarbeit.simplifx.reflect.utils.ExceptionSupplier;

public abstract class Reflection {

    static final BiPredicate<Class<?>, Class<?>> PRIMITIVE_CHECK = (wantedType, foundType) ->
            wantedType.isPrimitive() && ((Boolean.class.equals(foundType) && boolean.class.equals(wantedType))
                    || (Byte.class.equals(foundType) && byte.class.equals(wantedType))
                    || (Character.class.equals(foundType) && char.class.equals(wantedType))
                    || (Double.class.equals(foundType) && double.class.equals(wantedType))
                    || (Float.class.equals(foundType) && float.class.equals(wantedType))
                    || (Integer.class.equals(foundType) && int.class.equals(wantedType))
                    || (Long.class.equals(foundType) && long.class.equals(wantedType))
                    || (Short.class.equals(foundType) && short.class.equals(wantedType)));

    public static FieldReflection reflectStatic(Field field) {
        return new FieldReflection(field, null);
    }

    public static MethodReflection reflectStatic(Method method) {
        return new MethodReflection(method, null);
    }

    public static ConstructorReflection reflect(Constructor<?> constructor) {
        return new ConstructorReflection(constructor);
    }

    public static FieldReflection reflect(Field field, Object accessor) {
        return new FieldReflection(field, accessor);
    }

    public static MethodReflection reflect(Method method, Object accessor) {
        return new MethodReflection(method, accessor);
    }

    public static ClassReflection reflect(Class<?> clazz) {
        return new ClassReflection(clazz);
    }

    public static InstanceReflection reflect(Object instance) {
        return new InstanceReflection(instance);
    }

    @SuppressWarnings("unchecked")
    static <T> T unsafeCast(Object obj) {
        return (T) obj;
    }

    static <T extends AccessibleObject & Member, S extends ReflectiveOperationException>
    void makeAccessibleAndExecute(T member, Object accessor, boolean forceAccess, ExceptionRunnable<S> runnable) {
        if (forceAccess) {
            final boolean canAccess = member.canAccess(accessor);
            member.setAccessible(true);
            runnable.run();
            member.setAccessible(canAccess);
            return;
        }
        runnable.run();
    }

    static <T extends AccessibleObject & Member, S, U extends ReflectiveOperationException>
    S makeAccessibleAndExecute(T member, Object accessor, boolean forceAccess, ExceptionSupplier<S, U> supplier) {
        if (forceAccess) {
            final boolean canAccess = member.canAccess(accessor);
            member.setAccessible(true);
            final S result = supplier.get();
            member.setAccessible(canAccess);
            return result;
        }
        return supplier.get();
    }

    static <T, S extends ReflectiveOperationException> T handleReflectiveExceptions(ExceptionSupplier<T, S> supplier) {
        return supplier.get();
    }

}
