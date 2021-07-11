package de.intelligence.bachelorarbeit.simplifx.classpath;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.intelligence.bachelorarbeit.simplifx.classpath.source.DiscoverySourceType;
import de.intelligence.bachelorarbeit.simplifx.classpath.source.ResourceContext;
import de.intelligence.bachelorarbeit.simplifx.utils.Prefix;

public final class ClassDiscovery implements IClassDiscovery {

    private final DiscoveryContext context;
    private final Map<String, List<ResourceContext>> results;

    public ClassDiscovery(DiscoveryContext context) {
        this.context = context;
        this.results = new HashMap<>();
    }

    //TODO check if OS independence is still given
    private String convertPackage(String pkg) {
        return pkg.replace('\\', Prefix.FILE_SEPARATOR_C).replace('.', Prefix.FILE_SEPARATOR_C);
    }

    private void findAllClassPathURLs(String path, ClassLoader loader, Set<URL> classPathURLs) {
        try {
            loader.getResources(path).asIterator().forEachRemaining(classPathURLs::add);
        } catch (IOException ignored) {}
        if (path.isBlank()) {
            ClassLoader current = loader;
            do {
                if (current instanceof URLClassLoader) {
                    classPathURLs.addAll(Arrays.asList(((URLClassLoader) current).getURLs()));
                }
                if (current.equals(ClassLoader.getSystemClassLoader())) {
                    final String manifestPath = System.getProperty("java.class.path");
                    Arrays.stream(manifestPath.split(System.getProperty("path.separator"))).forEach(s -> {
                        try {
                            classPathURLs.add(new URL(Prefix.JAR_PREFIX + Prefix.FILE_PREFIX + new File(s)
                                    .getAbsolutePath() + Prefix.JAR_SEPARATOR));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    });
                }
                current = current.getParent();
            } while (current != null);
        }
    }

    @Override
    public IDiscoveryResult startDiscovery() {
        final Set<URL> classPathURLs = new HashSet<>();
        final String path = this.convertPackage(this.context.getPath());
        this.context.getClassLoaders().forEach(l -> this.findAllClassPathURLs(path, l, classPathURLs));
        classPathURLs.stream()
                .map(u -> DiscoverySourceType.createDiscoverySource(u, path))
                .flatMap(s -> s.iterator().stream())
                .forEach(ctx -> {
                    if (ctx.getFileType() != null) {
                        if (!this.results.containsKey(ctx.getFileType())) {
                            this.results.put(ctx.getFileType(), new ArrayList<>());
                        }
                        this.results.get(ctx.getFileType()).add(ctx);
                    }
                });
        return new DiscoveryResult(this.results);
    }

}
