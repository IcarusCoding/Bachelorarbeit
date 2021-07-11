package de.intelligence.bachelorarbeit.simplifx.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

public final class AnnotationUtils {

    private AnnotationUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T extends Annotation> void invokeMethodsByPrioritizedAnnotation(Object obj, Class<T> annotation,
                                                                                   Predicate<Method> condition,
                                                                                   Function<T, Integer> priority,
                                                                                   Object... params) {
        AnnotatedMethodCache.getMethodsAnnotatedBy(annotation, obj.getClass()).stream()
                .filter(condition).sorted(Comparator.<Method, Integer>comparing(m -> priority.apply(m.getAnnotation(annotation)))
                .reversed()).forEach(m -> Reflection.reflect(m, obj).forceAccess().invoke(params));

    }

}
