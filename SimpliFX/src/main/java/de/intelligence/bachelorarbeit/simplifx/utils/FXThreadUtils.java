package de.intelligence.bachelorarbeit.simplifx.utils;

import javafx.application.Platform;

import com.sun.javafx.application.PlatformImpl;

/**
 * An utility class for interacting with the JavaFX Thread.
 *
 * @author Deniz Groenhoff
 */
public final class FXThreadUtils {

    private FXThreadUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the specified {@link Runnable} on the JavaFX Thread.
     *
     * @param runnable The {@link Runnable} which will be run on the JavaFX Thread.
     */
    public static void runOnFXThread(Runnable runnable) {
        if (PlatformImpl.isFxApplicationThread()) {
            runnable.run();
            return;
        }
        Platform.runLater(runnable);
    }

    /**
     * Runs the specified {@link Runnable} on the JavaFX Thread and waits for the end of its execution.
     *
     * @param runnable The {@link Runnable} which will be run on the JavaFX Thread.
     */
    public static void waitOnFxThread(Runnable runnable) {
        PlatformImpl.runAndWait(runnable);
    }

}
