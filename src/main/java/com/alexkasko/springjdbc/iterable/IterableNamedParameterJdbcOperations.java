package com.alexkasko.springjdbc.iterable;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.Map;

/**
 * Extension interface for {@code NamedParameterJdbcOperations}. All methods, that return {@code List}
 * mirrored with {@code queryForIter} methods that return {@link CloseableIterator}.
 * Javadocs borrowed from {@code NamedParameterJdbcOperations}.
 *
 * @author alexkasko
 * Date: 11/7/12
 */
public interface IterableNamedParameterJdbcOperations {

    /**
   	 * Expose IterableJdbcTemplate to allow queries
   	 * without named parameters
   	 */
    IterableJdbcOperations getIterableJdbcOperations();

    /**
     * Query given SQL to create a prepared statement from SQL and a list
     * of arguments to bind to the query, mapping each row to a Java object
     * via a RowMapper.
     *
     * @param sql         SQL query to execute
     * @param paramSource container of arguments to bind to the query
     * @param rowMapper   object that will map one object per row
     * @return the result Iterator, containing mapped objects
     * @throws org.springframework.dao.DataAccessException
     *          if the query fails
     */
    <T> CloseableIterator<T> queryForIter(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a list
     * of arguments to bind to the query, mapping each row to a Java object
     * via a RowMapper.
     *
     * @param sql       SQL query to execute
     * @param paramMap  map of parameters to bind to the query
     *                  (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @param rowMapper object that will map one object per row
     * @return the result Iterator, containing mapped objects
     * @throws org.springframework.dao.DataAccessException
     *          if the query fails
     */
    <T> CloseableIterator<T> queryForIter(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result list.
     * <p>The results will be mapped to a List (one entry for each row) of
     * result objects, each of them matching the specified element type.
     *
     * @param sql         SQL query to execute
     * @param paramSource container of arguments to bind to the query
     * @param elementType the required type of element in the result list
     *                    (for example, <code>Integer.class</code>)
     * @return an Iterator of objects that match the specified element type
     * @throws org.springframework.dao.DataAccessException
     *          if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForList(String, Class)
     * @see org.springframework.jdbc.core.SingleColumnRowMapper
     */
    <T> CloseableIterator<T> queryForIter(String sql, SqlParameterSource paramSource, Class<T> elementType)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result list.
     * <p>The results will be mapped to a List (one entry for each row) of
     * result objects, each of them matching the specified element type.
     *
     * @param sql         SQL query to execute
     * @param paramMap    map of parameters to bind to the query
     *                    (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @param elementType the required type of element in the result list
     *                    (for example, <code>Integer.class</code>)
     * @return an Iterator of objects that match the specified element type
     * @throws org.springframework.dao.DataAccessException
     *          if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForList(String, Class)
     * @see org.springframework.jdbc.core.SingleColumnRowMapper
     */
    <T> CloseableIterator<T> queryForIter(String sql, Map<String, ?> paramMap, Class<T> elementType)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result list.
     * <p>The results will be mapped to a List (one entry for each row) of
     * Maps (one entry for each column, using the column name as the key).
     * Thus  Each element in the list will be of the form returned by this interface's
     * queryForMap() methods.
     *
     * @param sql         SQL query to execute
     * @param paramSource container of arguments to bind to the query
     * @return an Iterator that contains a Map per row
     * @throws org.springframework.dao.DataAccessException
     *          if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForList(String)
     */
    CloseableIterator<Map<String, Object>> queryForIter(String sql, SqlParameterSource paramSource) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result list.
     * <p>The results will be mapped to a List (one entry for each row) of
     * Maps (one entry for each column, using the column name as the key).
     * Each element in the list will be of the form returned by this interface's
     * queryForMap() methods.
     *
     * @param sql      SQL query to execute
     * @param paramMap map of parameters to bind to the query
     *                 (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @return an Iterator that contains a Map per row
     * @throws org.springframework.dao.DataAccessException
     *          if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForList(String)
     */
    CloseableIterator<Map<String, Object>> queryForIter(String sql, Map<String, ?> paramMap) throws DataAccessException;

}
