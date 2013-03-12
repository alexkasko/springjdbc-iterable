package com.alexkasko.springjdbc.iterable;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * {@code NamedParameterJdbcTemplate} extension. All methods, that return {@code List}
 * mirrored with {@code queryForIter} methods that return {@link CloseableIterator}.
 *
 * @author alexkasko
 *         Date: 11/7/12
 */
public class IterableNamedParameterJdbcTemplate extends NamedParameterJdbcTemplate implements IterableNamedParameterJdbcOperations {

    public IterableNamedParameterJdbcTemplate(DataSource dataSource) {
        super(new IterableJdbcTemplate(dataSource));
    }

    public IterableNamedParameterJdbcTemplate(DataSource dataSource, int fetchSize) {
        super(new IterableJdbcTemplate(dataSource, fetchSize));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IterableJdbcOperations getIterableJdbcOperations() {
        return (IterableJdbcOperations) getJdbcOperations();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws DataAccessException {
        return getIterableJdbcOperations().queryForIter(getPreparedStatementCreator(sql, paramSource), rowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws DataAccessException {
        return queryForIter(sql, new MapSqlParameterSource(paramMap), rowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, SqlParameterSource paramSource, Class<T> elementType) throws DataAccessException {
        return queryForIter(sql, paramSource, new SingleColumnRowMapper<T>(elementType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, Map<String, ?> paramMap, Class<T> elementType) throws DataAccessException {
        return queryForIter(sql, new MapSqlParameterSource(paramMap), elementType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableIterator<Map<String, Object>> queryForIter(String sql, SqlParameterSource paramSource) throws DataAccessException {
        return queryForIter(sql, paramSource, new ColumnMapRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableIterator<Map<String, Object>> queryForIter(String sql, Map<String, ?> paramMap) throws DataAccessException {
        return queryForIter(sql, new MapSqlParameterSource(paramMap));
    }
}
