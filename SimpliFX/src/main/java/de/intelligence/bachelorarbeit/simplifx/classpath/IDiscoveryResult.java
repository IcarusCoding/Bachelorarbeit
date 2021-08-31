package de.intelligence.bachelorarbeit.simplifx.classpath;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import de.intelligence.bachelorarbeit.simplifx.classpath.transformer.IResourceTypeTransformer;

public interface IDiscoveryResult {

    <T> List<T> transform(IResourceTypeTransformer<T> transformer);

    <T, S> List<S> transformAndFilter(IResourceTypeTransformer<T> transformer, Predicate<T> filter,
                                      Function<T, S> converter);

    List<Class<?>> findClassesAnnotatedBy(Class<? extends Annotation> annotation);

}
