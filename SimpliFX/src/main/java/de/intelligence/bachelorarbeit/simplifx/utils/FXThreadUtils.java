package de.intelligence.bachelorarbeit.simplifx.utils;

import javafx.application.Platform;

import com.sun.javafx.application.PlatformImpl;

public final class FXThreadUtils {

    private FXThreadUtils() {
        throw new UnsupportedOperationException();
    }

    public static void runOnFXThread(Runnable runnable) {
        if (PlatformImpl.isFxApplicationThread()) {
            runnable.run();
            return;
        }
        Platform.runLater(runnable);
    }

    public static void waitOnFxThread(Runnable runnable) {
        PlatformImpl.runAndWait(runnable);
    }

}
