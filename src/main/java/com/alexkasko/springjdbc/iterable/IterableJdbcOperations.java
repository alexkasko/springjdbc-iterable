package com.alexkasko.springjdbc.iterable;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import java.util.Map;

/**
 * Extension interface for {@code JdbcOperations}. All methods, that return {@code List}
 * mirrored with {@code queryForIter} methods that return {@link CloseableIterator}.
 * Javadocs borrowed from {@code JdbcOperations}.
 *
 * @author alexkasko
 *         Date: 11/7/12
 * @see IterableNamedParameterJdbcOperations
 */
public interface IterableJdbcOperations {
    /**
     * Query using a prepared statement, mapping each row to a Java object
     * via a RowMapper.
     * <p>A PreparedStatementCreator can either be implemented directly or
     * configured through a PreparedStatementCreatorFactory.
     *
     * @param psc       object that can create a PreparedStatement given a Connection
     *                  If this is null, the SQL will be assumed to contain no bind parameters.
     * @param rowMapper object that will map one object per row
     * @return the result Iterator, containing mapped objects
     * @throws DataAccessException if there is any problem
     * @see org.springframework.jdbc.core.PreparedStatementCreatorFactory
     */
    <T> CloseableIterator<T> queryForIter(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException;

    /**
     * Query using a prepared statement, mapping each row to a Java object
     * via a RowMapper.
     * <p>A PreparedStatementCreator can either be implemented directly or
     * configured through a PreparedStatementCreatorFactory.
     *
     * @param psc       object that can create a PreparedStatement given a Connection
     * @param pss       object that knows how to set values on the prepared statement.
     *                  If this is null, the SQL will be assumed to contain no bind parameters.
     * @param rowMapper object that will map one object per row
     * @return the result Iterator, containing mapped objects
     * @throws DataAccessException if there is any problem
     * @see org.springframework.jdbc.core.PreparedStatementCreatorFactory
     */
    <T> CloseableIterator<T> queryForIter(PreparedStatementCreator psc, PreparedStatementSetter pss, RowMapper<T> rowMapper)
            throws DataAccessException;

    /**
     * Execute a query given static SQL, mapping each row to a Java object
     * via a RowMapper.
     * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
     * execute a static query with a PreparedStatement, use the overloaded
     * <code>queryForIter</code> method with <code>null</code> as argument array.
     *
     * @param sql       SQL query to execute
     * @param rowMapper object that will map one object per row
     * @return the result Iterator, containing mapped objects
     * @throws DataAccessException if there is any problem executing the query
     * @see #queryForIter(String, Object[], RowMapper)
     */
    <T> CloseableIterator<T> queryForIter(String sql, RowMapper<T> rowMapper) throws DataAccessException;

    /**
     * Execute a query for a result шterator, given static SQL.
     * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
     * execute a static query with a PreparedStatement, use the overloaded
     * <code>queryForIter</code> method with <code>null</code> as argument array.
     * <p>The results will be mapped to a Iterator (one entry for each row) of
     * result objects, each of them matching the specified element type.
     *
     * @param sql         SQL query to execute
     * @param elementType the required type of element in the result шterator
     *                    (for example, <code>Integer.class</code>)
     * @return an Iterator of objects that match the specified element type
     * @throws DataAccessException if there is any problem executing the query
     * @see #queryForIter(String, Object[], Class)
     * @see org.springframework.jdbc.core.SingleColumnRowMapper
     */
    <T> CloseableIterator<T> queryForIter(String sql, Class<T> elementType) throws DataAccessException;

    /**
     * Execute a query for a result iterator, given static SQL.
     * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
     * execute a static query with a PreparedStatement, use the overloaded
     * <code>queryForIter</code> method with <code>null</code> as argument array.
     * <p>The results will be mapped to a Iterator (one entry for each row) of
     * Maps (one entry for each column using the column name as the key).
     * Each element in the iterator will be of the form returned by this interface's
     * queryForMap() methods.
     *
     * @param sql SQL query to execute
     * @return an Iterator that contains a Map per row
     * @throws DataAccessException if there is any problem executing the query
     * @see #queryForIter(String, Object[])
     */
    CloseableIterator<Map<String, Object>> queryForIter(String sql) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * PreparedStatementSetter implementation that knows how to bind values
     * to the query, mapping each row to a Java object via a RowMapper.
     *
     * @param sql       SQL query to execute
     * @param pss       object that knows how to set values on the prepared statement.
     *                  If this is <code>null</code>, the SQL will be assumed to contain no bind parameters.
     *                  Even if there are no bind parameters, this object may be used to
     *                  set fetch size and other performance options.
     * @param rowMapper object that will map one object per row
     * @return the result Iterator, containing mapped objects
     * @throws DataAccessException if the query fails
     */
    <T> CloseableIterator<T> queryForIter(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a list
     * of arguments to bind to the query, mapping each row to a Java object
     * via a RowMapper.
     *
     * @param sql       SQL query to execute
     * @param args      arguments to bind to the query
     * @param argTypes  SQL types of the arguments
     *                  (constants from <code>java.sql.Types</code>)
     * @param rowMapper object that will map one object per row
     * @return the result Iterator, containing mapped objects
     * @throws DataAccessException if the query fails
     * @see java.sql.Types
     */
    <T> CloseableIterator<T> queryForIter(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a list
     * of arguments to bind to the query, mapping each row to a Java object
     * via a RowMapper.
     *
     * @param sql       SQL query to execute
     * @param args      arguments to bind to the query
     *                  (leaving it to the PreparedStatement to guess the corresponding SQL type);
     *                  may also contain {@link org.springframework.jdbc.core.SqlParameterValue} objects which indicate not
     *                  only the argument value but also the SQL type and optionally the scale
     * @param rowMapper object that will map one object per row
     * @return the result Iterator, containing mapped objects
     * @throws DataAccessException if the query fails
     */
    <T> CloseableIterator<T> queryForIter(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a list
     * of arguments to bind to the query, mapping each row to a Java object
     * via a RowMapper.
     *
     * @param sql       SQL query to execute
     * @param rowMapper object that will map one object per row
     * @param args      arguments to bind to the query
     *                  (leaving it to the PreparedStatement to guess the corresponding SQL type);
     *                  may also contain {@link org.springframework.jdbc.core.SqlParameterValue} objects which indicate not
     *                  only the argument value but also the SQL type and optionally the scale
     * @return the result List, containing mapped objects
     * @throws DataAccessException if the query fails
     */
    <T> CloseableIterator<T> queryForIter(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result iterator.
     * <p>The results will be mapped to a Iterator (one entry for each row) of
     * result objects, each of them matching the specified element type.
     *
     * @param sql         SQL query to execute
     * @param args        arguments to bind to the query
     * @param argTypes    SQL types of the arguments
     *                    (constants from <code>java.sql.Types</code>)
     * @param elementType the required type of element in the result iterator
     *                    (for example, <code>Integer.class</code>)
     * @return a Iterator of objects that match the specified element type
     * @throws DataAccessException if the query fails
     * @see #queryForIter(String, Class)
     * @see org.springframework.jdbc.core.SingleColumnRowMapper
     */
    <T> CloseableIterator<T> queryForIter(String sql, Object[] args, int[] argTypes, Class<T> elementType)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result iterator.
     * <p>The results will be mapped to an Iterator (one entry for each row) of
     * result objects, each of them matching the specified element type.
     *
     * @param sql         SQL query to execute
     * @param args        arguments to bind to the query
     *                    (leaving it to the PreparedStatement to guess the corresponding SQL type);
     *                    may also contain {@link org.springframework.jdbc.core.SqlParameterValue} objects which indicate not
     *                    only the argument value but also the SQL type and optionally the scale
     * @param elementType the required type of element in the result iterator
     *                    (for example, <code>Integer.class</code>)
     * @return an Iterator of objects that match the specified element type
     * @throws DataAccessException if the query fails
     * @see #queryForIter(String, Class)
     * @see org.springframework.jdbc.core.SingleColumnRowMapper
     */
    <T> CloseableIterator<T> queryForIter(String sql, Object[] args, Class<T> elementType) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result iterator.
     * <p>The results will be mapped to an Iterator (one entry for each row) of
     * result objects, each of them matching the specified element type.
     *
     * @param sql         SQL query to execute
     * @param elementType the required type of element in the result iterator
     *                    (for example, <code>Integer.class</code>)
     * @param args        arguments to bind to the query
     *                    (leaving it to the PreparedStatement to guess the corresponding SQL type);
     *                    may also contain {@link org.springframework.jdbc.core.SqlParameterValue} objects which indicate not
     *                    only the argument value but also the SQL type and optionally the scale
     * @return an Iterator of objects that match the specified element type
     * @throws DataAccessException if the query fails
     * @see #queryForIter(String, Class)
     * @see org.springframework.jdbc.core.SingleColumnRowMapper
     */
    <T> CloseableIterator<T> queryForIter(String sql, Class<T> elementType, Object... args) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result iterator.
     * <p>The results will be mapped to an Iterator (one entry for each row) of
     * Maps (one entry for each column, using the column name as the key).
     * Thus  Each element in the iterator will be of the form returned by {@code JdbcOperations}'
     * queryForMap() methods.
     *
     * @param sql      SQL query to execute
     * @param args     arguments to bind to the query
     * @param argTypes SQL types of the arguments
     *                 (constants from <code>java.sql.Types</code>)
     * @return an Iterator that contains a Map per row
     * @throws DataAccessException if the query fails
     * @see #queryForIter(String)
     * @see java.sql.Types
     */
    CloseableIterator<Map<String, Object>> queryForIter(String sql, Object[] args, int[] argTypes) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result iterator.
     * <p>The results will be mapped to an Iterator (one entry for each row) of
     * Maps (one entry for each column, using the column name as the key).
     * Each element in the iterator will be of the form returned by this interface's
     * queryForMap() methods.
     *
     * @param sql  SQL query to execute
     * @param args arguments to bind to the query
     *             (leaving it to the PreparedStatement to guess the corresponding SQL type);
     *             may also contain {@link org.springframework.jdbc.core.SqlParameterValue} objects which indicate not
     *             only the argument value but also the SQL type and optionally the scale
     * @return an Iterator that contains a Map per row
     * @throws DataAccessException if the query fails
     * @see #queryForIter(String)
     */
    CloseableIterator<Map<String, Object>> queryForIter(String sql, Object... args) throws DataAccessException;
}