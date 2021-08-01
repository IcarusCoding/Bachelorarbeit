package de.intelligence.bachelorarbeit.simplifx.classpath;

import java.util.List;

public final class DiscoveryContext {

    private final String path;
    private final List<ClassLoader> classLoaders;

    public DiscoveryContext(String path, List<ClassLoader> classLoaders) {
        this.path = path;
        this.classLoaders = classLoaders;
    }

    public String getPath() {
        return this.path;
    }

    public List<ClassLoader> getClassLoaders() {
        return this.classLoaders;
    }

}
