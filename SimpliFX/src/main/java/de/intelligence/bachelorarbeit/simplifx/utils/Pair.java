package de.intelligence.bachelorarbeit.simplifx.utils;

/**
 * A class which encapsulates a single key and value pair like the {@link java.util.Map.Entry} class.
 *
 * @param <T> The type of the key.
 * @param <S> The type of the value.
 * @author Deniz Groenhoff
 */
public final class Pair<T, S> {

    private final T left;
    private final S right;

    private Pair(T left, S right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Creates a new {@link Pair} instance with the specified key-value pair.
     *
     * @param left  The key.
     * @param right The value.
     * @param <T>   The type of the key.
     * @param <S>   The type of the value.
     * @return The new {@link Pair} instance.
     */
    public static <T, S> Pair<T, S> of(T left, S right) {
        return new Pair<>(left, right);
    }

    /**
     * Retrieves the encapsulated key.
     *
     * @return The encapsulated key.
     */
    public T getLeft() {
        return this.left;
    }

    /**
     * Retrieves the encapsulated value.
     *
     * @return The encapsulated value.
     */
    public S getRight() {
        return this.right;
    }

}
