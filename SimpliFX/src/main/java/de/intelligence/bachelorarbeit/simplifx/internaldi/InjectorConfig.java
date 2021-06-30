package de.intelligence.bachelorarbeit.simplifx.internaldi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class InjectorConfig {

    private final Set<Class<?>> registeredClasses;
    private final Map<Class<?>, IFactory<?>> factoryMap;
    private final Map<Class<?>, Class<?>> bindingMap;

    protected InjectorConfig() {
        this.registeredClasses = new HashSet<>();
        this.factoryMap = new HashMap<>();
        this.bindingMap = new HashMap<>();
    }

    protected <T> void installFactory(Class<T> clazz, IFactory<T> factory) {
        if (!this.registeredClasses.contains(clazz)) {
            this.registeredClasses.add(clazz);
            this.factoryMap.put(clazz, factory);
        }
    }

    protected <T> void installBinding(Class<T> superClazz, Class<? extends T> implClazz) {
        if (!this.registeredClasses.contains(superClazz)) {
            this.registeredClasses.add(superClazz);
            this.bindingMap.put(superClazz, implClazz);
        }
    }

    Map<Class<?>, IFactory<?>> getFactoryMap() {
        return this.factoryMap;
    }

    Set<Class<?>> getRegisteredClasses() {
        return this.registeredClasses;
    }

    boolean isRegistered(Class<?> clazz) {
        return this.registeredClasses.contains(clazz);
    }

    boolean containsFactoryFor(Class<?> clazz) {
        return this.factoryMap.containsKey(clazz);
    }

    boolean containsBindingFor(Class<?> clazz) {
        return this.bindingMap.containsKey(clazz);
    }

    @SuppressWarnings("unchecked")
    <T> IFactory<T> getFactoryFor(Class<T> clazz) {
        return (IFactory<T>) this.factoryMap.get(clazz);
    }

    @SuppressWarnings("unchecked")
    <T> Class<? extends T> getBindingFor(Class<T> clazz) {
        return (Class<? extends T>) this.bindingMap.get(clazz);
    }

    protected abstract void setup();

}
