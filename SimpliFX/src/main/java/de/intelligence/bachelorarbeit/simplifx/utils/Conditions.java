package de.intelligence.bachelorarbeit.simplifx.utils;

import java.util.function.Consumer;

import lombok.experimental.UtilityClass;

/**
 * A utility class that provides basic conditional operations
 *
 * @author Deniz Groenhoff
 */
@UtilityClass
public final class Conditions {

    public <T> T checkNull(T t) {
        if (t == null) {
            throw new NullPointerException("Parameter is null.");
        }
        return t;
    }

    public static <T> void doIfNotNull(T t, ExceptionRunnable action, Consumer<Exception> onException) {
        if (t != null) {
            try {
                action.run();
            } catch (Exception ex) {
                onException.accept(ex);
            }
        }
    }

    public static <T> T nullOnException(ExceptionSupplier<T> action) {
        try {
            return action.get();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static <S, T> T returnIfNotNullReturn(S s, ExceptionSupplier<T> action) {
        if (s != null) {
            try {
                return action.get();
            } catch (Exception ignored) {}
        }
        return null;
    }

}
