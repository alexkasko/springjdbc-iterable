package com.alexkasko.springjdbc.iterable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;

/**
 * {@code JdbcTemplate} extension. All methods, that return {@code List}
 * mirrored with {@code queryForIter} methods that return {@link CloseableIterator}.
 *
 * @author alexkasko
 *         Date: 11/7/12
 */
public class IterableJdbcTemplate extends JdbcTemplate implements IterableJdbcOperations {
    private static final Log logger = LogFactory.getLog(IterableJdbcTemplate.class);

    /**
     * Constructor
     *
     * @param dataSource data source
     */
    public IterableJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Static method to use in finally methods for closing
     * {@link CloseableIterator}s. Writes warning into log on exception.
     *
     * @param iter iterator to close
     */
    public static void closeQuetly(CloseableIterator<?> iter) {
        try {
            if (iter != null) {
                iter.close();
            }
        } catch (Exception e) {
            logger.warn("Error on closing iterator: [" + iter + "]", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
        return queryForIter(psc, null, rowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // see {@code JdbcTemplate#query(org.springframework.jdbc.core.PreparedStatementCreator, org.springframework.jdbc.core.PreparedStatementSetter, org.springframework.jdbc.core.ResultSetExtractor)}
    // see {@code JdbcTemplate#execute(org.springframework.jdbc.core.PreparedStatementCreator, org.springframework.jdbc.core.PreparedStatementCallback)}
    // see {@code JdbcTemplate#query(org.springframework.jdbc.core.PreparedStatementCreator, org.springframework.jdbc.core.PreparedStatementSetter, org.springframework.jdbc.core.ResultSetExtractor)}
    public <T> CloseableIterator<T> queryForIter(PreparedStatementCreator psc, PreparedStatementSetter pss,
                                                 RowMapper<T> rowMapper) throws DataAccessException {
        Assert.notNull(psc, "PreparedStatementCreator must not be null");
        Assert.notNull(rowMapper, "RowMapper must not be null");
        if(logger.isDebugEnabled()) {
            String sql = getSql(psc);
            logger.debug("Executing prepared SQL statement" + (sql != null ? " [" + sql + "]" : ""));
        }
        DataSource ds = getDataSource();
        Connection con = DataSourceUtils.getConnection(ds);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Connection conToUse = con;
            NativeJdbcExtractor nje = getNativeJdbcExtractor();
            if(nje != null && nje.isNativeConnectionNecessaryForNativePreparedStatements()) {
                conToUse = nje.getNativeConnection(con);
            }
            ps = psc.createPreparedStatement(conToUse);
            applyStatementSettings(ps);
            PreparedStatement psToUse = ps;
            if(nje != null) {
                psToUse = nje.getNativePreparedStatement(ps);
            }
            if(pss != null) {
                pss.setValues(psToUse);
            }
            rs = psToUse.executeQuery();
            ResultSet rsToUse = rs;
            if(nje != null) {
                rsToUse = nje.getNativeResultSet(rs);
            }
            // warnings are handled after query execution but before data access
            handleWarnings(ps);
            return new PreparedStatementCloseableIterator<T>(ds, con, psc, pss, ps, rs, rsToUse, rowMapper);
        } catch(SQLException ex) {
            // Release Connection early, to avoid potential connection pool deadlock
            // in the case when the exception translator hasn't been initialized yet.
            if(psc instanceof ParameterDisposer) {
                ((ParameterDisposer) psc).cleanupParameters();
            }
            String sql = getSql(psc);
            psc = null;
            JdbcUtils.closeResultSet(rs);
            rs = null;
            JdbcUtils.closeStatement(ps);
            ps = null;
            DataSourceUtils.releaseConnection(con, getDataSource());
            con = null;
            throw getExceptionTranslator().translate("PreparedStatementCallback", sql, ex);
        }
//        resources will be closed in iterator
//        finally {
//            if(psc instanceof ParameterDisposer) {
//                ((ParameterDisposer) psc).cleanupParameters();
//            }
//            JdbcUtils.closeStatement(ps);
//            DataSourceUtils.releaseConnection(con, getDataSource());
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        Assert.hasText(sql, "Provided sql query is blank");
        Assert.notNull(rowMapper, "RowMapper must not be null");
        DataSource ds = getDataSource();
        Connection con = DataSourceUtils.getConnection(ds);
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Connection conToUse = con;
            NativeJdbcExtractor nje = getNativeJdbcExtractor();
            if(nje != null && nje.isNativeConnectionNecessaryForNativeStatements()) {
                conToUse = nje.getNativeConnection(con);
            }
            stmt = conToUse.createStatement();
            applyStatementSettings(stmt);
            Statement stmtToUse = stmt;
            if(nje != null) {
                stmtToUse = nje.getNativeStatement(stmt);
            }
            rs = stmtToUse.executeQuery(sql);
            ResultSet rsToUse = rs;
            if(nje != null) {
                rsToUse = nje.getNativeResultSet(rs);
            }
            // warnings are handled after query execution but before data access
            handleWarnings(stmt);
            return new StatementCloseableIterator<T>(ds, con, stmt, rs, rsToUse, rowMapper);
        } catch(SQLException ex) {
            JdbcUtils.closeResultSet(rs);
            rs = null;
            // Release Connection early, to avoid potential connection pool deadlock
            // in the case when the exception translator hasn't been initialized yet.
            JdbcUtils.closeStatement(stmt);
            stmt = null;
            DataSourceUtils.releaseConnection(con, getDataSource());
            con = null;
            throw getExceptionTranslator().translate("StatementCallback", getSql(sql), ex);
        }
//        resources will be closed in iterator
//   		finally {
//   			JdbcUtils.closeStatement(stmt);
//   			DataSourceUtils.releaseConnection(con, getDataSource());
//   		}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, Class<T> elementType) throws DataAccessException {
        return queryForIter(sql, getSingleColumnRowMapper(elementType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableIterator<Map<String, Object>> queryForIter(String sql) throws DataAccessException {
        return queryForIter(sql, getColumnMapRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        return queryForIter(new SimplePreparedStatementCreator(sql), pss, rowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        return queryForIter(sql, newArgTypePreparedStatementSetter(args, argTypes), rowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        return queryForIter(sql, newArgPreparedStatementSetter(args), rowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return queryForIter(sql, newArgPreparedStatementSetter(args), rowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException {
        return queryForIter(sql, args, argTypes, getSingleColumnRowMapper(elementType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, Object[] args, Class<T> elementType) throws DataAccessException {
        return queryForIter(sql, args, getSingleColumnRowMapper(elementType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CloseableIterator<T> queryForIter(String sql, Class<T> elementType, Object... args) throws DataAccessException {
        return queryForIter(sql, args, getSingleColumnRowMapper(elementType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableIterator<Map<String, Object>> queryForIter(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return queryForIter(sql, args, argTypes, getColumnMapRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableIterator<Map<String, Object>> queryForIter(String sql, Object... args) throws DataAccessException {
        return queryForIter(sql, args, getColumnMapRowMapper());
    }

    /**
     * Determine SQL from potential provider object.
     * Borrowed from {@code JdbcTemplate}
     *
     * @param sqlProvider object that's potentially a SqlProvider
     * @return the SQL string, or <code>null</code>
     * @see SqlProvider
     */
    private static String getSql(Object sqlProvider) {
        if(sqlProvider instanceof SqlProvider) {
            return ((SqlProvider) sqlProvider).getSql();
        } else {
            return null;
        }
    }

    /**
     * Simple adapter for PreparedStatementCreator, allowing to use a plain SQL statement.
     * Borrowed from {@code JdbcTemplate}
     */
    private static class SimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
        private final String sql;
        public SimplePreparedStatementCreator(String sql) {
            Assert.notNull(sql, "SQL must not be null");
            this.sql = sql;
        }
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            return con.prepareStatement(this.sql);
        }
        public String getSql() {
            return this.sql;
        }
    }
}
