package de.intelligence.bachelorarbeit.simplifx.utils;

/**
 * An functional interface like {@link Runnable} with exception support
 *
 * @author Deniz Groenhoff
 * @see Runnable
 */
@FunctionalInterface
public interface ExceptionRunnable {

    void run() throws Exception;

}
