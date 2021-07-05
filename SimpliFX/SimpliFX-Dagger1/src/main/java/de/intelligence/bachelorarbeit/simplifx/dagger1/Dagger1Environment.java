package de.intelligence.bachelorarbeit.simplifx.dagger1;

import dagger.ObjectGraph;

import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;

final class Dagger1Environment implements DIEnvironment {

    private final ObjectGraph graph;

    Dagger1Environment(Object obj, Object... modules) {
        this.graph = ObjectGraph.create(modules);
        this.inject(obj);
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
