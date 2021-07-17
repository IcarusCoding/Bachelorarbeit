package de.intelligence.bachelorarbeit.simplifx.shared;

import java.util.HashMap;
import java.util.Map;

import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

public final class SharedResources {

    private final Object lock;
    private final Map<String, SharedReference<?>> referenceMap;

    public SharedResources() {
        this.lock = new Object();
        this.referenceMap = new HashMap<>();
    }

    public boolean contains(String name) {
        synchronized (this.lock) {
            return this.referenceMap.containsKey(name);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> SharedReference<T> getForName(String name) {
        synchronized (this.lock) {
            return (SharedReference<T>) this.referenceMap.get(name);
        }
    }

    public <T> void create(String name, SharedReference<T> ref) {
        synchronized (this.lock) {
            Conditions.checkNull(ref, "reference must not be null!");
            if (!this.referenceMap.containsKey(name)) {
                this.referenceMap.put(name, ref);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void set(String name, T obj) {
        synchronized (this.lock) {
            Conditions.checkNull(obj, "instance must not be null!");
            if (!this.referenceMap.containsKey(name)) {
                this.referenceMap.put(name, SharedReference.of(obj));
                return;
            }
            final Class<?> clazz = this.referenceMap.get(name).getType();
            if (!clazz.isAssignableFrom(obj.getClass())) {
                throw new RuntimeException("Illegal type");
            }
            final SharedReference<T> ref = (SharedReference<T>) this.referenceMap.get(name);
            ref.set(obj);
        }
    }

}
