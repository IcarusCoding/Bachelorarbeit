package de.intelligence.bachelorarbeit.simplifx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

public final class GuiceEnvironment implements DIEnvironment {

    private final Injector injector;

    public GuiceEnvironment(Object obj, Module... modules) {
        this.injector = Guice.createInjector(Conditions.concat(modules, new AbstractModule() {
            //TODO maybe add module with instances generated from SimpliFX (I18N etc.)
        }));
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
