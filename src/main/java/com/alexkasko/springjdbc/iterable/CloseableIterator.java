package com.alexkasko.springjdbc.iterable;

import java.util.Iterator;

/**
 * Iterator, that must be closed after use
 *
 * @author alexkasko
 * Date: 11/7/12
 */
public interface CloseableIterator<T> extends Iterator<T> {

    /**
     * Closes this iterator and releases JDBC resources associated
     * with it. If the iterator is already closed then invoking this
     * method has no effect.
     */
    void close();

    /**
     * @return whether iterator was closed
     */
    boolean isClosed();
}
