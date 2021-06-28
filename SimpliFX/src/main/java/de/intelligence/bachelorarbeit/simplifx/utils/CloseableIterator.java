package de.intelligence.bachelorarbeit.simplifx.utils;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Defines an extension to the {@link Iterator} interface. Adds closing abilities from {@link AutoCloseable}
 *
 * @author Deniz Groenhoff
 * @see java.util.Iterator
 * @see java.lang.AutoCloseable
 */
public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {

    @Override
    boolean hasNext();

    @Override
    T next();

    @Override
    void close();

    /**
     * Encapsulates this {@link Iterator} in an {@link java.util.Spliterator} and uses that in a {@link Stream}
     * If the {@link Stream} instance closes, {@link #close()} will be invoked
     *
     * @return A {@link Stream} representing this {@link Iterator}
     */
    default Stream<T> stream() {
        return StreamSupport.stream(Spliterators.spliterator(this, 0, 0), false)
                .onClose(this::close);
    }

}
