package de.intelligence.bachelorarbeit.reflectionutils;

import java.util.function.Supplier;

/**
 * The {@link ExceptionSupplier} class is an extension to the {@link Supplier} interface,
 * which supports basic exception handling
 *
 * @author Deniz Groenhoff
 */
@FunctionalInterface
public interface ExceptionSupplier<T, U extends Exception> {

    default T get(IReflectionExceptionHandler handler) {
        try {
            return getWithException();
        } catch (Exception ex) {
            if (handler == null) {
               // throw new ReflectionException(ex.getCause() == null ? ex : ex.getCause());
                throw new ReflectionException(ex);
            }
            handler.handleException(ex);
        }
        return null;
    }

    T getWithException() throws U;

}
