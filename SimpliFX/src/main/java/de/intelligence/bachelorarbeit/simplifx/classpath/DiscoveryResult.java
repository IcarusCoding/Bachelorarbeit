package de.intelligence.bachelorarbeit.simplifx.classpath;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.intelligence.bachelorarbeit.simplifx.classpath.filter.ClassAnnotationDiscoveryFilter;
import de.intelligence.bachelorarbeit.simplifx.classpath.filter.IDiscoveryFilter;
import de.intelligence.bachelorarbeit.simplifx.classpath.source.ResourceContext;
import de.intelligence.bachelorarbeit.simplifx.classpath.transformer.ClassResourceTransformer;
import de.intelligence.bachelorarbeit.simplifx.classpath.transformer.IResourceTypeTransformer;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;
import de.intelligence.bachelorarbeit.simplifx.utils.Prefix;

public final class DiscoveryResult implements IDiscoveryResult {

    private final Map<String, List<ResourceContext>> results;

    public DiscoveryResult(Map<String, List<ResourceContext>> results) {
        this.results = new HashMap<>(results);
    }

    public <T> List<T> transform(IResourceTypeTransformer<T> transformer) {
        return transformer.getSupportedExtensions().stream().map(results::get).filter(Objects::nonNull)
                .flatMap(List::stream).map(transformer::transform).collect(Collectors.toList());
    }

    // TODO replace full transformer and filter classes with function and predicate interface when needed
    @Override
    public <T, S> List<S> transformAndFilter(IResourceTypeTransformer<T> transformer, IDiscoveryFilter<T> filter,
                                             Function<T, S> converter) {
        return this.transform(transformer).stream().filter(filter::matches).map(converter).collect(Collectors.toList());
    }

    @Override
    public List<Class<?>> findClassesAnnotatedBy(Class<? extends Annotation> annotations) {
        return transformAndFilter(new ClassResourceTransformer(),
                new ClassAnnotationDiscoveryFilter(annotations),
                reader -> Conditions.nullOnException(() -> Class.forName(reader.getClassName().replace(
                        Prefix.FILE_SEPARATOR_C, '.'))));
    }

}
