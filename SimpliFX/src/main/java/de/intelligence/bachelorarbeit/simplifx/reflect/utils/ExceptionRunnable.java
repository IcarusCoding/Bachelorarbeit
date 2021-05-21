package de.intelligence.bachelorarbeit.simplifx.reflect.utils;

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
