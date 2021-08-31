package de.intelligence.bachelorarbeit.simplifx.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

import de.intelligence.bachelorarbeit.reflectionutils.MethodReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

/**
 * An utility class which provides annotation related operations.
 *
 * @author Deniz Groenhoff
 */
public final class AnnotationUtils {

    private AnnotationUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Invokes all methods in the specified instance by the provided annotation.
     *
     * @param obj        The instance from which the methods should be called.
     * @param annotation The annotation class.
     * @param callNoArgs If the no argument constructor should be called even if parameters are supplied to this method.
     * @param params     The parameters of the called methods.
     * @param <T>        The type of the {@link Annotation}.
     */
    public static <T extends Annotation> void invokeMethodsByAnnotation(Object obj, Class<T> annotation,
                                                                        boolean callNoArgs, Object... params) {
        AnnotationUtils.invokeMethodsByAnnotation(obj, annotation, a -> 0, callNoArgs, params);
    }

    /**
     * Invokes all methods in the specified instance by the provided annotation.
     *
     * @param obj        The instance from which the methods should be called.
     * @param annotation The annotation class.
     * @param <T>        The type of the {@link Annotation}.
     */
    public static <T extends Annotation> void invokeMethodsByAnnotation(Object obj, Class<T> annotation) {
        AnnotationUtils.invokeMethodsByAnnotation(obj, annotation, a -> 0);
    }

    /**
     * Invokes all methods in the specified instance by the provided annotation.
     *
     * @param obj        The instance from which the methods should be called.
     * @param annotation The annotation class.
     * @param priority   The priority which determines the method call order.
     * @param <T>        The type of the {@link Annotation}.
     */
    public static <T extends Annotation> void invokeMethodsByAnnotation(Object obj, Class<T> annotation,
                                                                        ToIntFunction<T> priority) {
        AnnotationUtils.invokeMethodsByAnnotation(obj, annotation, priority, true);
    }

    /**
     * Invokes all methods in the specified instance by the provided annotation.
     *
     * @param obj        The instance from which the methods should be called.
     * @param annotation The annotation class.
     * @param priority   The priority which determines the method call order.
     * @param callNoArgs If the no argument constructor should be called even if parameters are supplied to this method.
     * @param params     The parameters of the called methods.
     * @param <T>        The type of the {@link Annotation}.
     */
    public static <T extends Annotation> void invokeMethodsByAnnotation(Object obj, Class<T> annotation,
                                                                        ToIntFunction<T> priority,
                                                                        boolean callNoArgs, Object... params) {
        final List<Method> methods = AnnotatedMethodCache.getMethodsAnnotatedBy(annotation, obj.getClass());
        methods.stream().filter(m -> (callNoArgs && m.getParameterCount() == 0) || params.length == m.getParameterCount())
                .filter(m -> Reflection.matchArguments(m.getParameterTypes(),
                        Arrays.stream(params).map(Object::getClass).toArray(Class[]::new)))
                .sorted(Comparator.<Method, Integer>comparing(m -> priority.applyAsInt(m.getAnnotation(annotation))).reversed())
                .forEach(m -> {
                    final MethodReflection methodRef = Reflection.reflect(m, obj).forceAccess();
                    if (m.getParameterCount() == 0) {
                        methodRef.invoke();
                    } else {
                        methodRef.invoke(params);
                    }
                });
    }

}
