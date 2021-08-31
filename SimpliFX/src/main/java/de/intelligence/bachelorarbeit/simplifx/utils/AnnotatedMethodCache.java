package de.intelligence.bachelorarbeit.simplifx.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

/**
 * A utility class which caches methods by its annotation and class.
 *
 * @author Deniz Groenhoff
 */
public final class AnnotatedMethodCache {

    private static final ConcurrentHashMap<Class<? extends Annotation>, ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Method>>> cache;

    static {
        cache = new ConcurrentHashMap<>();
    }

    private AnnotatedMethodCache() {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieves the methods annotated with the specified annotation class in the provided class.
     *
     * @param annotationClazz The class of the annotation.
     * @param clazz           The class in which the methods should be found.
     * @return A {@link List} of methods annotated with the specified annotation int the provided class.
     */
    public static List<Method> getMethodsAnnotatedBy(Class<? extends Annotation> annotationClazz, Class<?> clazz) {
        AnnotatedMethodCache.cache.putIfAbsent(annotationClazz, new ConcurrentHashMap<>());
        final var classMethodMap = AnnotatedMethodCache.cache.get(annotationClazz);
        if (classMethodMap.containsKey(clazz)) {
            return new ArrayList<>(classMethodMap.get(clazz));
        }
        final CopyOnWriteArrayList<Method> methods = new CopyOnWriteArrayList<>();
        Reflection.reflect(clazz).iterateMethods(m -> m.isAnnotationPresent(annotationClazz), methods::add);
        classMethodMap.put(clazz, methods);
        return new ArrayList<>(methods);
    }

}
