package de.intelligence.bachelorarbeit.simplifx.utils;

/**
 * Defines an {@link Iterable} which contains an auto closeable {@link CloseableIterator}
 *
 * @author Deniz Groenhoff
 * @see java.lang.Iterable
 * @see AutoCloseable
 * @see CloseableIterator
 */
public interface CloseableIterable<T> extends Iterable<T> {

    @Override
    CloseableIterator<T> iterator();

}
