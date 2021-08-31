package de.intelligence.bachelorarbeit.simplifx.experimental;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;

import com.sun.javafx.application.PlatformImpl;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

public final class ExperimentalInterceptor {

    /**
     * Will be called from intercepted void methods.
     *
     * @param obj         The object in which the interception occurred.
     * @param superMethod The real implementation of the intercepted method in the superclass.
     * @param params      The parameters of the intercepted method.
     */
    public static void interceptVoid(Object obj, Method superMethod, Object... params) {
        Platform.runLater(() -> Reflection.reflect(superMethod, obj).invoke(params));
    }

    /**
     * Will be called from intercepted methods which return a value.
     *
     * @param obj         The object in which the interception occurred.
     * @param superMethod The real implementation of the intercepted method in the superclass.
     * @param params      The parameters of the intercepted method.
     * @return The return value of the intercepted method.
     */
    public static Object interceptAndReturn(Object obj, Method superMethod, Object... params) {
        final AtomicReference<Object> o = new AtomicReference<>();
        PlatformImpl.runAndWait(() -> o.set(Reflection.reflect(superMethod, obj).invoke(params)));
        return o.get();
    }

}
