package de.intelligence.bachelorarbeit.simplifx.internaldi;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.exception.InjectorException;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

public final class Injector {

    private final InjectorConfig config;

    public Injector(InjectorConfig config) {
        Conditions.checkNull(config);
        this.config = config;
        this.config.setup();
    }

    public <T> T get(Class<T> clazz) {
        Conditions.checkNull(clazz);
        if (this.config.containsInstanceFor(clazz)) {
            return this.config.getInstanceFor(clazz);
        }
        if (this.config.containsFactoryFor(clazz)) {
            return this.config.getFactoryFor(clazz).get();
        }
        if (this.config.containsBindingFor(clazz)) {
            return createInstanceOf(this.config.getBindingFor(clazz));
        }
        return createInstanceOf(clazz);
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstanceOf(Class<T> clazz) {
        if (this.config.containsInstanceFor(clazz)) {
            return this.config.getInstanceFor(clazz);
        }
        AtomicReference<Constructor<?>> ref = new AtomicReference<>();
        Reflection.reflect(clazz).iterateConstructors(
                c -> c.getParameterCount() == 0 || (c.isAnnotationPresent(Inject.class) && Arrays
                        .stream(c.getParameterTypes()).allMatch(cl -> {
                            final AtomicBoolean hasDefaultConstructor = new AtomicBoolean();
                            Reflection.reflect(cl).iterateConstructors(con -> con.getParameterCount() == 0,
                                    con -> hasDefaultConstructor.set(true));
                            return hasDefaultConstructor.get() || this.config.isRegistered(cl);
                        })), con -> {
                    if (ref.get() == null) {
                        ref.set(con);
                    }
                });
        if (ref.get() == null) {
            throw new InjectorException("Could not create instance of " + clazz.getSimpleName());
        }
        final Class<?>[] parameterTypes = ref.get().getParameterTypes();
        final Object[] objects = new Object[parameterTypes.length];
        for (int i = 0; i < objects.length; i++) {
            final Class<?> currentParam = parameterTypes[i];
            if (this.config.containsInstanceFor(currentParam)) {
                objects[i] = this.config.getInstanceFor(currentParam);
                continue;
            }
            if (this.config.containsFactoryFor(currentParam)) {
                objects[i] = this.config.getFactoryFor(currentParam).get();
                continue;
            }
            if (this.config.containsBindingFor(currentParam)) {
                return (T) this.createInstanceOf(currentParam);
            }
            objects[i] = Reflection.reflect(currentParam).findConstructor().forceAccess().instantiate();
        }
        return Reflection.reflect(ref.get()).instantiateUnsafeAndGet(objects);
    }

}
