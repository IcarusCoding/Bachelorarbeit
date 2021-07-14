package de.intelligence.bachelorarbeit.simplifx.shared;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SharedResources {

    private final Map<String, SharedReference<?>> referenceMap;

    public SharedResources() {
        this.referenceMap = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> SharedReference<T> getForName(String name) {
        return (SharedReference<T>) referenceMap.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> void set(String name, T obj) {
        if (!referenceMap.containsKey(name)) {
            referenceMap.put(name, SharedReference.of(obj));
            // update every registered controller
            return;
        }
        Class<?> clazz = referenceMap.get(name).getType();
        if (!clazz.isAssignableFrom(obj.getClass())) {
            throw new RuntimeException("Illegal type");
        }
        SharedReference<T> ref = (SharedReference<T>) referenceMap.get(name);
        ref.set(obj);
    }

}
