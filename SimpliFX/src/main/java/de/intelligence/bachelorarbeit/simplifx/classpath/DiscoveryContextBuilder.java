package de.intelligence.bachelorarbeit.simplifx.classpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DiscoveryContextBuilder {

    private final List<ClassLoader> classLoaders;
    private String path;

    public DiscoveryContextBuilder() {
        this.path = "";
        this.classLoaders = new ArrayList<>();
    }

    public DiscoveryContext build() {
        return new DiscoveryContext(this.path, this.classLoaders);
    }

    public DiscoveryContextBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public DiscoveryContextBuilder addClassLoader(ClassLoader classLoader) {
        this.classLoaders.add(classLoader);
        return this;
    }

    public DiscoveryContextBuilder addClassLoaders(ClassLoader... classLoaders) {
        this.classLoaders.addAll(Arrays.asList(classLoaders));
        return this;
    }

    public DiscoveryContextBuilder setDefaultClassLoaders() {
        this.classLoaders.clear();
        ClassLoader classLoader;
        if ((classLoader = DiscoveryContextBuilder.class.getClassLoader()) != null) {
            this.classLoaders.add(classLoader);
        }
        if ((classLoader = Thread.currentThread().getContextClassLoader()) != null) {
            this.classLoaders.add(classLoader);
        }
        return this;
    }

}
