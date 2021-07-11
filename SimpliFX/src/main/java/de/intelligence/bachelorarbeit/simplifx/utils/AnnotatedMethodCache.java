package de.intelligence.bachelorarbeit.simplifx.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

public final class AnnotatedMethodCache {

    private static final ConcurrentHashMap<Class<? extends Annotation>, ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Method>>> cache;

    static {
        cache = new ConcurrentHashMap<>();
    }

    public static List<Method> getMethodsAnnotatedBy(Class<? extends Annotation> annotationClazz, Class<?> clazz) {
        if (!AnnotatedMethodCache.cache.containsKey(annotationClazz)) {
            AnnotatedMethodCache.cache.put(annotationClazz, new ConcurrentHashMap<>());
        }
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
