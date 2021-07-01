package de.intelligence.bachelorarbeit.reflectionutils;

/**
 * The {@link ExceptionRunnable} class is an extension to the {@link Runnable} interface,
 * which supports basic exception handling
 *
 * @author Deniz Groenhoff
 */
@FunctionalInterface
public interface ExceptionRunnable<T extends Exception> {

    default void run(IReflectionExceptionHandler handler) {
        try {
            runWithException();
        } catch (Exception ex) {
            handler.handleException(ex);
        }
    }

    void runWithException() throws T;

}
