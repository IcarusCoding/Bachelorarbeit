package de.intelligence.bachelorarbeit.simplifx.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import de.intelligence.bachelorarbeit.reflectionutils.FieldReflection;

/**
 * An interface which provides functionalities related to handling and injecting values into annotated fields.
 *
 * @param <T> The annotation type.
 */
public interface IAnnotatedFieldDetector<T extends Annotation> {

    /**
     * Finds all fields which are annotated by the generic annotation type of this instance.
     */
    void findAllFields();

    /**
     * Finds all fields which are annotated by the generic annotation type of this instance
     * and filters them by the specified {@link BiPredicate}.
     */
    void findAllFields(BiPredicate<FieldReflection, T[]> predicate);

    /**
     * Injects a value into all found fields.
     *
     * @param value             The value which should get injected.
     * @param forceAccess       If the injection should happen for not accessible fields (like private ones).
     * @param exceptionConsumer The {@link BiConsumer} for exception handling purposes.
     */
    void injectValue(Object value, boolean forceAccess, BiConsumer<Field, Exception> exceptionConsumer);

    /**
     * Retrieves all found annotated fields with their respective instance.
     *
     * @return A {@link Map} containing all found annotated fields with their respective instance.
     */
    Map<Object, Map<Field, T[]>> getFieldMap();

    /**
     * Retrieves all annotated fields from the specified instance.
     *
     * @param obj The instance for which the annotated fields should get retrieved.
     * @return A {@link Map} containing all annotated fields from the specified instance.
     */
    Map<Field, T[]> getAnnotatedFields(Object obj);

    /**
     * Retrieves all found annotations.
     *
     * @return A {@link Set} containing all found annotations.
     */
    Set<T> getAnnotations();

    /**
     * Retrieves all found fields.
     *
     * @return A {@link Set} containing all found fields.
     */
    Set<Field> getFields();

}