package de.intelligence.bachelorarbeit.simplifx.shared;

import java.util.concurrent.atomic.AtomicReference;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public final class SharedReference<T> {

    private final AtomicReference<T> ref;
    private final ReadOnlyObjectWrapper<T> refWrap;
    private Class<?> type;

    private SharedReference(T t) {
        this.ref = new AtomicReference<>(t);
        this.refWrap = new ReadOnlyObjectWrapper<>(t);
        this.type = t.getClass();
    }

    public static <T> SharedReference<T> of(T t) {
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