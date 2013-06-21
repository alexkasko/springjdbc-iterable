package com.alexkasko.springjdbc.iterable;


import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * Specialized {@link Iterable} partial implementation, returns closable iterators.
 * This class is not used directly in this library and may be used for
 * processing possible results for multiple queries in "passive" mode
 * (no DB resources are used until client call to {@link #iterator()}).
 * Retains references to returned iterators and closes them on {@link #close()} call.
 *
 *
 * @author alexkasko
 * Date: 6/21/13
 */
public abstract class CloseableIterable<T> implements Iterable<T>, Closeable {
    protected List<CloseableIterator<T>> iters = new ArrayList<CloseableIterator<T>>();

    /**
     * Implementation should return new {@link CloseableIterator} instance
     *
     * @return closeable iterator
     */
    protected abstract CloseableIterator<T> closeableIterator();

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableIterator<T> iterator() {
        CloseableIterator<T> ci = closeableIterator();
        iters.add(ci);
        return ci;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        for (CloseableIterator<T> ci : iters) {
            if (null != ci) ci.close();
        }
    }

    /**
     * Returns {@code true} if all produces iterators are closed
     *
     * @return {@code true} if all produces iterators are closed
     *         {@code false} otherwise
     */
    public boolean isClosed() {
        for (CloseableIterator<T> ci : iters) {
            if (null != ci && !ci.isClosed()) return false;
        }
        return true;
    }
}
