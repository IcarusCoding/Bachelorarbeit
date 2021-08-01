package de.intelligence.bachelorarbeit.simplifx.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import de.intelligence.bachelorarbeit.reflectionutils.FieldReflection;

public interface IAnnotatedFieldDetector<T extends Annotation> {

    void findAllFields();

    void findAllFields(BiPredicate<FieldReflection, T[]> predicate);

    void injectValue(Object value, boolean forceAccess, BiConsumer<Field, Exception> exceptionConsumer);

    Map<Object, Map<Field, T[]>> getFieldMap();

    Map<Field, T[]> getAnnotatedFields(Object obj);

    Set<T> getAnnotations();

    List<Field> getFields();

}