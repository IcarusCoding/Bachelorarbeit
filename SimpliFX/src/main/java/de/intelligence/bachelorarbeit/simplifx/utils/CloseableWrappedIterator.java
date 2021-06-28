package de.intelligence.bachelorarbeit.simplifx.utils;

import java.util.Iterator;

import lombok.extern.log4j.Log4j2;

/**
 * An implementation of the {@link CloseableIterator} interface, which delegates
 * its method calls to an underlying {@link Iterator}
 *
 * @author Deniz Groenhoff
 * @see java.util.Iterator
 * @see de.intelligence.bachelorarbeit.simplifx.utils.CloseableIterator
 */
@Log4j2
public final class CloseableWrappedIterator<T> implements CloseableIterator<T> {

    private final Iterator<T> delegate;

    /**
     * Creates a new {@link CloseableWrappedIterator} with an {@link Iterator} to
     * which all function invocations get delegated to
     *
     * @param delegate The delegate {@link Iterator}
     */
    public CloseableWrappedIterator(Iterator<T> delegate) {
        this.delegate = Conditions.checkNull(delegate);
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public T next() {
        T t = delegate.next();
        if (t == null) {
            System.out.println("ERR");
            System.exit(-1);
        }
        return t;
    }

    @Override
    public void close() {
        if (this.delegate instanceof AutoCloseable) {
            try {
                ((AutoCloseable) this.delegate).close();
            } catch (Exception ex) {
                log.error("Failed to close iterator: ", ex);
            }
        }
    }

}
