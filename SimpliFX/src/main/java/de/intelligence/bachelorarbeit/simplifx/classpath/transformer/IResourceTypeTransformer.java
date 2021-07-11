package de.intelligence.bachelorarbeit.simplifx.classpath.transformer;

import java.util.Set;

import de.intelligence.bachelorarbeit.simplifx.classpath.source.ResourceContext;

public interface IResourceTypeTransformer<S> {

    S transform(ResourceContext ctx);

    Set<String> getSupportedExtensions();

}
