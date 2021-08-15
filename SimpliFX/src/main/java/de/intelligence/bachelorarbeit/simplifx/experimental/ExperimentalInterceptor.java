package de.intelligence.bachelorarbeit.simplifx.experimental;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;

import com.sun.javafx.application.PlatformImpl;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

public final class ExperimentalInterceptor {

    public static void interceptVoid(Object obj, Method superMethod, Object... params) {
        Platform.runLater(() -> Reflection.reflect(superMethod, obj).invoke(params));
    }

    public static Object interceptAndReturn(Object obj, Method superMethod, Object... params) {
        final AtomicReference<Object> o = new AtomicReference<>();
        PlatformImpl.runAndWait(() -> o.set(Reflection.reflect(superMethod, obj).invoke(params)));
        return o.get();
    }

}
