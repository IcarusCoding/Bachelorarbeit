package de.intelligence.bachelorarbeit.simplifx.utils;

/**
 * An functional interface like {@link java.util.function.Supplier} with exception support
 *
 * @author Deniz Groenhoff
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ExceptionSupplier<T> {

    T get() throws Exception;

}
