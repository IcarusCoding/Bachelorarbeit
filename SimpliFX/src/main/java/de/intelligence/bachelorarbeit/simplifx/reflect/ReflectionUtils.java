package de.intelligence.bachelorarbeit.simplifx.reflect;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

/**
 * Utility class for comfortably accessing and using the Reflection API
 *
 * @author Deniz Groenhoff
 */
@UtilityClass
public final class ReflectionUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVES;

    static {
        PRIMITIVES = new HashMap<>();
        PRIMITIVES.put(Void.class, void.class);
        PRIMITIVES.put(Boolean.class, boolean.class);
        PRIMITIVES.put(Byte.class, byte.class);
        PRIMITIVES.put(Character.class, char.class);
        PRIMITIVES.put(Short.class, short.class);
        PRIMITIVES.put(Integer.class, int.class);
        PRIMITIVES.put(Float.class, float.class);
        PRIMITIVES.put(Double.class, double.class);
        PRIMITIVES.put(Long.class, long.class);
    }

    private static <T extends AccessibleObject & Member, S extends ReflectiveOperationException> void makeAccessibleAndExecute(T member, Object accessor, ExceptionRunnable<S> runnable) {
        final boolean canAccess = member.canAccess(accessor);
        member.setAccessible(true);
        runnable.run();
        member.setAccessible(canAccess);
    }

    private static <T extends AccessibleObject & Member, S, U extends ReflectiveOperationException> S makeAccessibleAndExecute(T member, Object accessor, ExceptionSupplier<S, U> supplier) {
        final boolean canAccess = member.canAccess(accessor);
        member.setAccessible(true);
        final S result = supplier.get();
        member.setAccessible(canAccess);
        return result;
    }

    private static <T> T doPrivileged(PrivilegedAction<T> action) {
        return AccessController.doPrivileged(action);
    }

    @SuppressWarnings("unchecked")
    private static <T> T unsafeCast(Object obj) {
        return (T) obj;
    }

    private static <T, S extends ReflectiveOperationException> T handleReflectiveExceptions(ExceptionSupplier<T, S> supplier) {
        return supplier.get();
    }

    @Nullable
    public static <T> T invokeStatic(Method method) {
        return invoke(method, null);
    }

    @Nullable
    public static <T> T invokeStatic(Method method, @Nullable Object... args) {
        return invoke(method, null, args);
    }

    @Nullable
    public static <T> T invoke(Method method, @Nullable Object accessor) {
        return invoke(method, accessor, new Object[] {});
    }

    @Nullable
    public static <T> T invoke(Method method, @Nullable Object accessor, @Nullable Object... args) {
        return doPrivileged(() -> makeAccessibleAndExecute(method, accessor, () -> unsafeCast(method.invoke(accessor, args))));
    }

    public static void findMethods(Class<?> clazz, @Nullable Identifier<Method> identifier, Callback<Method> callback) {
        Arrays.stream(clazz.getDeclaredMethods()).filter(m -> identifier != null && identifier.check(m)).forEach(callback::callback);
        if(clazz.isInterface()) {
            Arrays.stream(clazz.getInterfaces()).forEach(m -> findMethods(clazz, identifier, callback));
            return;
        }
        if(clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            findMethods(clazz.getSuperclass(), identifier, callback);
        }
    }

    public static void setStatic(Field field, @Nullable Object value) {
        set(field, null, value);
    }

    public static void set(Field field, @Nullable Object accessor, @Nullable Object value) {
        doPrivileged(() -> {
            makeAccessibleAndExecute(field, accessor, () -> field.set(accessor, value));
            return null;
        });
    }

    @Nullable
    public static <T> T getStatic(Field field) {
        return get(field, null);
    }

    @Nullable
    public static <T> T get(Field field, @Nullable Object accessor) {
        return doPrivileged(() -> makeAccessibleAndExecute(field, accessor, () -> unsafeCast(field.get(accessor))));
    }

    public static <T extends AnnotatedElement> boolean isAnnotatedBy(T element, Class<? extends Annotation> annotationClass) {
        return element.isAnnotationPresent(annotationClass);
    }

    @Nullable
    public <T extends AnnotatedElement, S extends Annotation> S getAnnotation(T element, Class<S> annotationClass) {
        return element.getAnnotation(annotationClass);
    }

    @Nullable
    public Field findField(Class<?> clazz, String name) {
        return handleReflectiveExceptions(() -> clazz.getDeclaredField(name));
    }

    @Nullable
    public Method findMethod(Class<?> clazz, String name, Object... args) {
        return handleReflectiveExceptions(() -> clazz.getDeclaredMethod(name, Arrays.stream(args).map(Object::getClass)
                .map(c -> PRIMITIVES.getOrDefault(c, c)).toArray(Class<?>[]::new)));
    }

    @FunctionalInterface
    public interface Identifier<T> {
        boolean check(T t);
    }

    @FunctionalInterface
    public interface Callback<T> {
        void callback(T t);
    }

    @FunctionalInterface
    private interface ExceptionRunnable<T extends Exception> extends Runnable {

        @Override
        default void run() {
            try {
                runWithException();
            } catch (Exception ex) {
                ex.printStackTrace(); // TODO remove
            }
        }

        void runWithException() throws T;

    }

    @FunctionalInterface
    private interface ExceptionSupplier<T, U extends Exception> extends Supplier<T> {

        @Override
        default T get() {
            try {
                return getWithException();
            } catch (Exception ex) {
                ex.printStackTrace(); // TODO remove
            }
            return null;
        }

        T getWithException() throws U;

    }

}
