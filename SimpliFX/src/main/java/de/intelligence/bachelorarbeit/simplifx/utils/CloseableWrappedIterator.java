package de.intelligence.bachelorarbeit.simplifx.utils;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An implementation of the {@link CloseableIterator} interface, which delegates
 * its method calls to an underlying {@link Iterator}
 *
 * @author Deniz Groenhoff
 * @see java.util.Iterator
 * @see de.intelligence.bachelorarbeit.simplifx.utils.CloseableIterator
 */
public final class CloseableWrappedIterator<T> implements CloseableIterator<T> {

    private static final Logger LOG = LogManager.getLogger(CloseableWrappedIterator.class);

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
            this.close();
        }
        return t;
    }

    @Override
    public void close() {
        if (this.delegate instanceof AutoCloseable) {
            try {
                ((AutoCloseable) this.delegate).close();
            } catch (Exception ex) {
                CloseableWrappedIterator.LOG.error("Failed to close iterator", ex);
            }
        }
    }

}
