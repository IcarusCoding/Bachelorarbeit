package de.intelligence.bachelorarbeit.simplifx.classpath.transformer;

import java.io.IOException;
import java.util.Set;

import org.objectweb.asm.ClassReader;

import de.intelligence.bachelorarbeit.simplifx.classpath.ClasspathDiscoveryException;
import de.intelligence.bachelorarbeit.simplifx.classpath.source.ResourceContext;

public final class ClassResourceTransformer implements IResourceTypeTransformer<ClassReader> {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("class");

    @Override
    public ClassReader transform(ResourceContext ctx) {
        try {
            return new ClassReader(ctx.getRelativeURI().toString().replace(".class", "")
                    .replace('\\', '.').replace('/', '.'));
        } catch (IOException ex) {
            throw new ClasspathDiscoveryException("Error while transforming: " + ctx.getRelativeURI(), ex);
        }
    }

    @Override
    public Set<String> getSupportedExtensions() {
        return ClassResourceTransformer.SUPPORTED_EXTENSIONS;
    }

}
