package de.intelligence.bachelorarbeit.reflectionutils;

import java.util.function.Supplier;

/**
 * The {@link ExceptionSupplier} class is an extension to the {@link Supplier} interface,
 * which supports basic exception handling
 *
 * @author Deniz Groenhoff
 */
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
