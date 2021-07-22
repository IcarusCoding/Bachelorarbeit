package de.intelligence.bachelorarbeit.simplifx.shared;

import java.util.concurrent.atomic.AtomicReference;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

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

    public static <T> SharedReference<T> empty(Class<?> type) {
        return new SharedReference<>(type);
    }

    public static <T> SharedReference<T> of(T t) {
        Conditions.checkNull(t, "parameter must not be null.");
        return new SharedReference<>(t);
    }

    public Class<?> getType() {
        return this.type;
    }

    public T get() {
        return this.ref.get();
    }

    public void set(T t) {
        this.ref.set(t);
        this.refWrap.set(t);
    }

    public ReadOnlyObjectProperty<T> asProperty() {
        return this.refWrap.getReadOnlyProperty();
    }

}