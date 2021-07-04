package de.intelligence.bachelorarbeit.simplifx.dagger1;

import dagger.ObjectGraph;

import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;

public final class Dagger1Environment implements DIEnvironment {

    private final Object module;

    private final ObjectGraph graph;

    public Dagger1Environment(Object obj, Object module, Object... modules) {
        this.module = module;
        this.graph = null;
    }

    @Override
    public void inject(Object instance) {

    }

    @Override
    public <T> T get(Class<T> clazz) {
        return null;
    }

}
