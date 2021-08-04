package de.intelligence.bachelorarbeit.simplifx.shared;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

/**
 * A registry which manages {@link SharedReference} instances.
 *
 * @author Deniz Groenhoff
 */
public final class SharedResources {

    private final Map<String, SharedReference<?>> referenceMap;

    /**
     * Creates a new instance of this {@link SharedReference} registry.
     */
    public SharedResources() {
        this.referenceMap = new ConcurrentHashMap<>();
    }

    /**
     * Checks if a {@link SharedReference} with the specified name exists.
     *
     * @param name The name of the {@link SharedReference}.
     * @return If a {@link SharedReference} with the specified name exists.
     */
    public boolean contains(String name) {
        return this.referenceMap.containsKey(name);
    }

    /**
     * Retrieves a {@link SharedReference} by its name.
     *
     * @param name The name of the {@link SharedReference}.
     * @param <T>  The type of the {@link SharedReference}.
     * @return The {@link SharedReference} with the specified name or null if none was found.
     */
    @SuppressWarnings("unchecked")
    public <T> SharedReference<T> getForName(String name) {
        return (SharedReference<T>) this.referenceMap.get(name);
    }

    /**
     * Saves a new {@link SharedReference} in this {@link SharedResources} instance.
     *
     * @param name The name of the {@link SharedReference}.
     * @param ref  The {@link SharedReference} which should be saved.
     * @param <T>  The type of the {@link SharedReference}.
     */
    public <T> void create(String name, SharedReference<T> ref) {
        Conditions.checkNull(ref, "reference must not be null!");
        if (!this.referenceMap.containsKey(name)) {
            this.referenceMap.put(name, ref);
        }
    }

    /**
     * Sets the value of the {@link SharedReference} identified by the specified name.
     *
     * @param name The name of the {@link SharedReference} for which the value should get updated.
     * @param obj  The new value of the {@link SharedReference}.
     * @param <T>  The type of the {@link SharedReference}.
     */
    @SuppressWarnings("unchecked")
    public <T> void set(String name, T obj) {
        Conditions.checkNull(obj, "instance must not be null!");
        if (!this.contains(name)) {
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
