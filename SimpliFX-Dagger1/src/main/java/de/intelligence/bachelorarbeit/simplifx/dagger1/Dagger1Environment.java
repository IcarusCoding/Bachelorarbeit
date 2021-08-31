package de.intelligence.bachelorarbeit.simplifx.dagger1;

import dagger.ObjectGraph;

import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;

/**
 * An implementation of the {@link DIEnvironment} interface which manages a dagger {@link ObjectGraph}
 * for dependency injection.
 */
final class Dagger1Environment implements DIEnvironment {

    private final ObjectGraph graph;

    /**
     * Creates a new instance of this {@link DIEnvironment}.
     *
     * @param modules An array of dagger configuration classes.
     */
    Dagger1Environment(Object... modules) {
        this.graph = ObjectGraph.create(modules);
    }

    @Override
    public void inject(Object instance) {
        this.graph.inject(instance);
    }

    @Override
    public <T> T get(Class<T> clazz) {
        return this.graph.get(clazz);
    }

}
