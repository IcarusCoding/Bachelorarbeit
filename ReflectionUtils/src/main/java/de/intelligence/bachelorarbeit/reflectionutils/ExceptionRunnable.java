package de.intelligence.bachelorarbeit.reflectionutils;

/**
 * The {@link ExceptionRunnable} class is an extension to the {@link Runnable} interface,
 * which supports basic exception handling
 *
 * @author Deniz Groenhoff
 */
@FunctionalInterface
public interface ExceptionRunnable<T extends Exception> extends Runnable {

    @Override
    default void run() {
        try {
            runWithException();
        } catch (Exception ex) {
            ex.printStackTrace(); // TODO remove
        }
    }

    void runWithException() throws T;

}
