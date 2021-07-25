package de.intelligence.bachelorarbeit.reflectionutils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

import lombok.experimental.UtilityClass;

import sun.misc.Unsafe;

/**
 * Utility class for accessing the Reflection API in a simple way.
 *
 * @author Deniz Groenhoff
 */
@UtilityClass
public class Reflection {

    static final BiPredicate<Class<?>, Class<?>> PRIMITIVE_CHECK = (wantedType, foundType) ->
            wantedType.isPrimitive() && ((Boolean.class.equals(foundType) && boolean.class.equals(wantedType))
                    || (Byte.class.equals(foundType) && byte.class.equals(wantedType))
                    || (Character.class.equals(foundType) && char.class.equals(wantedType))
                    || (Double.class.equals(foundType) && double.class.equals(wantedType))
                    || (Float.class.equals(foundType) && float.class.equals(wantedType))
                    || (Integer.class.equals(foundType) && int.class.equals(wantedType))
                    || (Long.class.equals(foundType) && long.class.equals(wantedType))
                    || (Short.class.equals(foundType) && short.class.equals(wantedType)));

    /**
     * Starts the reflection with a static {@link Field} as the entry point
     *
     * @param field The entry point
     * @return A {@link FieldReflection} instance representing the entry point
     */
    public static FieldReflection reflectStatic(Field field) {
        return new FieldReflection(field, null);
    }

    /**
     * Starts the reflection with a static {@link Method} as the entry point
     *
     * @param method The entry point
     * @return A {@link MethodReflection} instance representing the entry point
     */
    public static MethodReflection reflectStatic(Method method) {
        return new MethodReflection(method, null);
    }

    /**
     * Starts the reflection with a {@link Constructor} as the entry point
     *
     * @param constructor The entry point
     * @return A {@link ConstructorReflection} instance representing the entry point
     */
    public static ConstructorReflection reflect(Constructor<?> constructor) {
        return new ConstructorReflection(constructor);
    }

    /**
     * Starts the reflection with a {@link Field} as the entry point
     *
     * @param field The entry point
     * @return A {@link FieldReflection} instance representing the entry point
     */
    public static FieldReflection reflect(Field field, Object accessor) {
        return new FieldReflection(field, accessor);
    }

    /**
     * Starts the reflection with a {@link Method} as the entry point
     *
     * @param method The entry point
     * @return A {@link MethodReflection} instance representing the entry point
     */
    public static MethodReflection reflect(Method method, Object accessor) {
        return new MethodReflection(method, accessor);
    }

    /**
     * Starts the reflection with a {@link Class} as the entry point
     *
     * @param clazz The entry point
     * @return A {@link ClassReflection} instance representing the entry point
     */
    public static ClassReflection reflect(Class<?> clazz) {
        return new ClassReflection(clazz);
    }

    /**
     * Starts the reflection with any {@link Object} as the entry point
     *
     * @param instance The entry point
     * @return A {@link InstanceReflection} instance representing the entry point
     */
    public static InstanceReflection reflect(Object instance) {
        return new InstanceReflection(instance);
    }

    public static void addOpens(List<String> fullyQualifiedPackageNames, String module, Module currentModule)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        final Unsafe unsafe = Reflection.reflect(Unsafe.class).reflectField("theUnsafe").forceAccess().getUnsafe();
        if (unsafe == null) {
            throw new ReflectionException("Could not retrieve unsafe!");
        }
        final ModuleLayer bootModuleLayer = Reflection.reflect(Class.forName("java.lang.ModuleLayer"))
                .reflectMethod("boot").invokeUnsafe();
        final Optional<Module> moduleOpt = bootModuleLayer.findModule(module);
        if (moduleOpt.isEmpty()) {
            throw new ReflectionException("Could not find module with name " + module);
        }
        final Module fMod = moduleOpt.get();
        final Class<?> moduleImpl = Class.forName("java.lang.Module");
        final Method addOpensMethodImpl = Reflection.reflect(moduleImpl)
                .reflectMethod("implAddOpens", String.class, moduleImpl).getReflectable();
        long firstFieldOffset = unsafe.objectFieldOffset(OffsetProvider.class.getDeclaredField("firstField"));
        unsafe.putBooleanVolatile(addOpensMethodImpl, firstFieldOffset, true);
        for (final String pkgName : fullyQualifiedPackageNames) {
            addOpensMethodImpl.invoke(fMod, pkgName, currentModule);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T unsafeCast(Object obj) {
        return (T) obj;
    }

    static <T extends AccessibleObject & Member, S extends ReflectiveOperationException>
    void makeAccessibleAndExecute(T member, Object accessor, boolean forceAccess, IReflectionExceptionHandler handler,
                                  ExceptionRunnable<S> runnable) {
        if (forceAccess) {
            final boolean canAccess = member.canAccess(accessor);
            member.setAccessible(true);
            runnable.run(handler);
            member.setAccessible(canAccess);
            return;
        }
        runnable.run(handler);
    }

    static <T extends AccessibleObject & Member, S, U extends ReflectiveOperationException>
    S makeAccessibleAndExecute(T member, Object accessor, boolean forceAccess, IReflectionExceptionHandler handler,
                               ExceptionSupplier<S, U> supplier) {
        if (forceAccess) {
            final boolean canAccess = member.canAccess(accessor);
            member.setAccessible(true);
            final S result = supplier.get(handler);
            member.setAccessible(canAccess);
            return result;
        }
        return supplier.get(handler);
    }

    static <T, S extends ReflectiveOperationException> T handleReflectiveExceptions(IReflectionExceptionHandler handler,
                                                                                    ExceptionSupplier<T, S> supplier) {
        return supplier.get(handler);
    }

    static <T extends ExceptionHandleable> T setExceptionHandler(T handleable, IReflectionExceptionHandler handler) {
        handleable.setExceptionHandler(handler);
        return handleable;
    }

    public static boolean matchArguments(Class<?>[] wanted, Class<?>[] actual) {
        boolean found = true;
        for (var index = 0; index < wanted.length; index++) {
            final Class<?> wantedType = wanted[index];
            final Class<?> actualType = actual[index];
            if (!actualType.isAssignableFrom(wantedType)) {
                found = PRIMITIVE_CHECK.test(actualType, wantedType);
            }
            if (!found) {
                break;
            }
        }
        return found;
    }
}
