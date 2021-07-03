package de.intelligence.bachelorarbeit.simplifx.utils;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public <T> T checkNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }

    public void checkCondition(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("Illegal argument specified.");
        }
    }

    public void checkCondition(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void doIfNotNull(T t, Runnable action) {
        if (t != null) {
            action.run();
        }
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

    public static <T> T[] concat(T[] first, T val) {
        T[] result = Arrays.copyOf(first, first.length + 1);
        result[first.length] = val;
        return result;
    }

    public static <T> Predicate<T> distinct(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

}
