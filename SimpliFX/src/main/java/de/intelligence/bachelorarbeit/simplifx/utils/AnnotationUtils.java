package de.intelligence.bachelorarbeit.simplifx.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

public final class AnnotationUtils {

    private AnnotationUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T extends Annotation> void invokeMethodsByAnnotation(Object obj, Class<T> annotation, boolean invokeInSuperclasses,
                                                                        boolean callNoArgs, Object... params) {
        AnnotationUtils.invokeMethodsByAnnotation(obj, annotation, a -> 0, invokeInSuperclasses, callNoArgs, params);
    }

    public static <T extends Annotation> void invokeMethodsByAnnotation(Object obj, Class<T> annotation, boolean invokeInSuperclasses) {
        AnnotationUtils.invokeMethodsByAnnotation(obj, annotation, a -> 0, invokeInSuperclasses);
    }

    public static <T extends Annotation> void invokeMethodsByAnnotation(Object obj, Class<T> annotation,
                                                                        Function<T, Integer> priority,
                                                                        boolean invokeInSuperclasses) {
        AnnotationUtils.invokeMethodsByAnnotation(obj, annotation, priority, invokeInSuperclasses, true);
    }

    public static <T extends Annotation> void invokeMethodsByAnnotation(Object obj, Class<T> annotation,
                                                                        Function<T, Integer> priority,
                                                                        boolean invokeInSuperclasses,
                                                                        boolean callNoArgs, Object... params) {
        final List<Method> methods;
        if (invokeInSuperclasses) {
            methods = Stream.iterate(obj.getClass(), Objects::nonNull, (UnaryOperator<Class<?>>) Class::getSuperclass)
                    .flatMap(c -> AnnotatedMethodCache.getMethodsAnnotatedBy(annotation, c).stream()).toList();
        } else {
            methods = AnnotatedMethodCache.getMethodsAnnotatedBy(annotation, obj.getClass());
        }
        methods.stream().filter(m -> (callNoArgs && m.getParameterCount() == 0) || params.length == m.getParameterCount())
                .filter(m -> Reflection.matchArguments(m.getParameterTypes(),
                        Arrays.stream(params).map(Object::getClass).toArray(Class[]::new)))
                .sorted(Comparator.<Method, Integer>comparing(m -> priority.apply(m.getAnnotation(annotation))).reversed())
                .forEach(m -> {
                    if (m.getParameterCount() == 0) {
                        Reflection.reflect(m, obj).forceAccess().invoke();
                        return;
                    }
                    Reflection.reflect(m, obj).forceAccess().invoke(params);
                });
    }

}
