package de.intelligence.bachelorarbeit.simplifx.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;

final class GuiceEnvironment implements DIEnvironment {

    private final Injector injector;

    GuiceEnvironment(Object obj, Module... modules) {
        this.injector = Guice.createInjector(modules);
        this.inject(obj);
    }

    @Override
    public void inject(Object obj) {
        this.injector.injectMembers(obj);
    }

    @Override
    public <T> T get(Class<T> clazz) {
        return this.injector.getInstance(clazz);
    }

}
