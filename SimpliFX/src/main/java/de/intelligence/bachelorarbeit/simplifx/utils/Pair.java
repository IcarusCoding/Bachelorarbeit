package de.intelligence.bachelorarbeit.simplifx.utils;

public final class Pair<T, S> {

    private final T left;
    private final S right;

    private Pair(T left, S right) {
        this.left = left;
        this.right = right;
    }

    public static <T, S> Pair<T, S> of(T left, S right) {
        return new Pair<>(left, right);
    }

    public T getLeft() {
        return this.left;
    }

    public S getRight() {
        return this.right;
    }

}
