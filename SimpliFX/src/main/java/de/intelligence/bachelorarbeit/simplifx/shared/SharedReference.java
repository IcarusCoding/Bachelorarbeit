package de.intelligence.bachelorarbeit.simplifx.shared;

import java.util.concurrent.atomic.AtomicReference;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

/**
 * A class which encapsulates a reference to an object.
 *
 * @param <T> The type of the reference.
 * @author Deniz Groenhoff
 */
public final class SharedReference<T> {

    private final AtomicReference<T> ref;
    private final ReadOnlyObjectWrapper<T> refWrap;
    private final Class<?> type;

    private SharedReference(Class<?> type) {
        this.ref = new AtomicReference<>();
        this.refWrap = new ReadOnlyObjectWrapper<>();
        this.type = type;
    }

    private SharedReference(T t) {
        this.ref = new AtomicReference<>(t);
        this.refWrap = new ReadOnlyObjectWrapper<>(t);
        this.type = t.getClass();
    }

    /**
     * Creates a new empty {@link SharedReference} with the specified class type.
     *
     * @param type The class type of the {@link SharedReference}.
     * @param <T>  The type of the {@link SharedReference}.
     * @return The newly created empty {@link SharedReference}.
     */
    public static <T> SharedReference<T> empty(Class<?> type) {
        return new SharedReference<>(type);
    }

    /**
     * Creates a new {@link SharedReference}.
     *
     * @param t   The initial value of the {@link SharedReference}.
     * @param <T> The type of the {@link SharedReference}.
     * @return The created {@link SharedReference}.
     */
    public static <T> SharedReference<T> of(T t) {
        Conditions.checkNull(t, "parameter must not be null.");
        return new SharedReference<>(t);
    }

    /**
     * Retrieves the class type of this {@link SharedReference} instance.
     *
     * @return The type of this {@link SharedReference} instance.
     */
    public Class<?> getType() {
        return this.type;
    }

    /**
     * Retrieves the current value of this {@link SharedReference}.
     *
     * @return The current value of this {@link SharedReference}.
     */
    public T get() {
        return this.ref.get();
    }

    /**
     * Sets the new value of this {@link SharedReference}.
     *
     * @param t The new value of this {@link SharedReference}.
     */
    public void set(T t) {
        this.ref.set(t);
        this.refWrap.set(t);
    }

    /**
     * Retrieves the updatable value of this {@link SharedReference} as an {@link ReadOnlyObjectProperty}.
     *
     * @return The updatable value of this {@link SharedReference} as an {@link ReadOnlyObjectProperty}.
     */
    public ReadOnlyObjectProperty<T> asProperty() {
        return this.refWrap.getReadOnlyProperty();
    }

}
