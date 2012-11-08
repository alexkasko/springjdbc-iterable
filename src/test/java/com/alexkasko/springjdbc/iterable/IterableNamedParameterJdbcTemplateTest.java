package com.alexkasko.springjdbc.iterable;

import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 11/8/12
 */
public class IterableNamedParameterJdbcTemplateTest {
    private static final IterableNamedParameterJdbcTemplate jt;

    static {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        jt = new IterableNamedParameterJdbcTemplate(ds);
    }

    @Test
    public void test() {
        jt.getJdbcOperations().update("create table foo(val varchar(255))");
        jt.getJdbcOperations().update("insert into foo(val) values('foo')");
        jt.getJdbcOperations().update("insert into foo(val) values('bar')");
        jt.getJdbcOperations().update("insert into foo(val) values('baz')");
//        test static sql
        CloseableIterator<String> iterStatic = jt.getIterableJdbcOperations().queryForIter(
                        "select val from foo where val like 'b%' order by val",
                        String.class);
        validateIter(iterStatic);
//        test prepared statement (no guava in this project)
        Map<String, String> params = new HashMap<String, String>();
        params.put("val", "b%");
        CloseableIterator<String> iterPrepared = jt.queryForIter(
                "select val from foo where val like :val order by val",
                params, String.class);
        validateIter(iterPrepared);
    }

    private void validateIter(CloseableIterator<String> iter) {
        assertFalse("Open fail", iter.isClosed());
        List<String> list = new ArrayList<String>();
        while (iter.hasNext()) list.add(iter.next());
        assertTrue("Close fail", iter.isClosed());
        assertEquals("Size fail", 2, list.size());
        assertEquals("Data fail", "bar", list.get(0));
        assertEquals("Data fail", "baz", list.get(1));
    }
}
