package de.intelligence.bachelorarbeit.simplifx.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;

/**
 * An implementation of the {@link DIEnvironment} interface which manages a guice {@link Injector}
 * for dependency injection.
 */
final class GuiceEnvironment implements DIEnvironment {

    private final Injector injector;

    /**
     * Creates a new instance of this {@link DIEnvironment}.
     *
     * @param modules An array of guice {@link Module} instances.
     */
    GuiceEnvironment(Module... modules) {
        this.injector = Guice.createInjector(modules);
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
