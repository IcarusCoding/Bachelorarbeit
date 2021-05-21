package de.intelligence.bachelorarbeit.simplifx.reflect.utils;

import java.util.function.Supplier;

@FunctionalInterface
public interface ExceptionSupplier<T, U extends Exception> extends Supplier<T> {

    @Override
    default T get() {
        try {
            return getWithException();
        } catch (Exception ex) {
            ex.printStackTrace(); // TODO remove
        }
        return null;
    }

    T getWithException() throws U;

}
