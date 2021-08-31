package de.intelligence.bachelorarbeit.simplifx.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import de.intelligence.bachelorarbeit.reflectionutils.FieldReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

/**
 * An implementation of the {@link IAnnotatedFieldDetector} interface which allows the detection and injection
 * into fields of multiple object instances.
 *
 * @param <T> The annotation type.
 */
public final class AnnotatedFieldDetector<T extends Annotation> implements IAnnotatedFieldDetector<T> {

    private final Class<T> annotation;
    private final List<Object> searchIn;
    private final Map<Object, Map<Field, T[]>> foundFields;

    /**
     * Creates a new {@link AnnotatedFieldDetector} with the specified annotation and object instances.
     *
     * @param annotation The annotation for which fields should get detected.
     * @param obj        The instance in which these fields should be found.
     * @param more       More instances in which fields should be found.
     */
    public AnnotatedFieldDetector(Class<T> annotation, Object obj, Object... more) {
        this.annotation = annotation;
        this.searchIn = Arrays.stream(Conditions.concat(more, obj)).filter(Objects::nonNull).collect(Collectors.toList());
        this.foundFields = new HashMap<>();
    }

    private void find(BiPredicate<FieldReflection, T[]> predicate) {
        this.foundFields.clear();
        this.searchIn.forEach(obj -> Reflection.reflect(obj).iterateFields(fieldReflection ->
                        fieldReflection.getReflectable().getAnnotationsByType(this.annotation).length > 0
                                && predicate.test(fieldReflection,
                                fieldReflection.getReflectable().getAnnotationsByType(this.annotation)),
                fieldReflection -> {
                    if (!this.foundFields.containsKey(obj)) {
                        this.foundFields.put(obj, new HashMap<>());
                    }
                    this.foundFields.get(obj).put(fieldReflection.getReflectable(),
                            fieldReflection.getReflectable().getAnnotationsByType(this.annotation));
                }));
    }

    @Override
    public void findAllFields() {
        this.find((t, a) -> true);
    }

    @Override
    public void findAllFields(BiPredicate<FieldReflection, T[]> predicate) {
        this.find(predicate);
    }

    @Override
    public void injectValue(Object value, boolean forceAccess, BiConsumer<Field, Exception> exceptionConsumer) {
        this.foundFields.forEach((obj, map) -> map.keySet().forEach(f -> {
            final FieldReflection fieldReflection = Reflection.reflect(f, obj);
            fieldReflection.setExceptionHandler(ex -> exceptionConsumer.accept(f, ex));
            if (forceAccess) {
                fieldReflection.forceAccess();
            }
            fieldReflection.set(value);
        }));
    }

    @Override
    public Map<Object, Map<Field, T[]>> getFieldMap() {
        return Collections.unmodifiableMap(this.foundFields);
    }

    @Override
    public Map<Field, T[]> getAnnotatedFields(Object obj) {
        if (!this.foundFields.containsKey(obj)) {
            return new HashMap<>();
        }
        return Collections.unmodifiableMap(this.foundFields.get(obj));
    }

    @Override
    public Set<T> getAnnotations() {
        return this.foundFields.values().stream().flatMap(m -> m.values().stream()).flatMap(Arrays::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Field> getFields() {
        return this.foundFields.values().stream().flatMap(m -> m.keySet().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

}
