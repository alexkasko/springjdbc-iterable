Spring JdbcTemplate extension that returns iterators
====================================================

This library extends `JdbcTemplate` and `NamedParameterJdbcTemplate` (from [spring-jdbc](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html))
allowing to return query results as `java.util.Iterator`'s.
All spring's `query` and `queryForList` methods which return `java.util.List`'s are mirrored with `queryForIter` methods.

In runtime library has no additional dependencies (except spring-jdbc itself and it's own dependencies).

Library is available in [Maven cental](http://repo1.maven.org/maven2/com/alexkasko/springjdbc/).

Javadocs for the latest release are available [here](http://alexkasko.github.com/springjdbc-iterable/javadocs).

JdbcTemplate problem
--------------------

`JdbcTemplate` and `NamedParameterJdbcTemplate` do a great job as an abstraction layer over JDBC.
Among other things they provide convenient methods to get a result set from DB as a List of app objects:

    List<MyObj> list = jt.query("select ...", params, mapper)

When list is returned by `jt` all JDBC resources (`ResultSet`, `Statement` and `Connection`) are already
properly released (exact operations depend on environment) and list is detached from DB.

If you need to process big result set, that should not be fully loaded into memory:

    Stat statistics = jt.query("select ...", params, new ResultSetExtractor<Stat>() {
        @Override
        public Stat extractData(ResultSet rs) throws SQLException, DataAccessException {
            while(rs.next) {
            ...
            }
        }});

This looks not such convenient, as previous example. But things become even worse when you need to load multiple big
result sets and process them simultaneously. You may call `jt` another time in `ResultSetExtractor` and process
its results in another `ResultSetExtractor` but java's syntax for closures made this not concise and separate classes or
even real closures from java 8 won't help much.

Solution
--------

Solution is to extend `JdbcTemplate` to support this method:

    CloseableIterator<MyObj> iter = jt.queryForIter("select ...", params, mapper);

With this method you may open multiple results sets as iterators and process them in the same method.
All iterators are connected to DB through open result sets and load data from DB as you iterate over
(more precisely, some JDBC drivers tend to load all result set into memory at once on default settings,
but usually you can control this using `jt.setFetchSize`).

`CloseableIterator` is an `java.util.Iterator` (so you may use [guava](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/collect/Iterators.html)
 methods on it), with `close()` method that must be called after using to release JDBC resources.
These iterators are closed automatically on result set exhaustion. But even if you real iterators fully,
you should always call `close()` in `finally` block for the case of exception throwing when iterator is still open.

In transactional environment (with any implementation of `PlatformTransactionManager`) open JDBC resources will
be valid only within transaction bounds. So such iterators must be opened, used and closed within single transaction.

Library usage
-------------

Maven dependency (available in central repository):

    <dependency>
        <groupId>com.alexkasko.springjdbc</groupId>
        <artifactId>springjdbc-iterable</artifactId>
        <version>1.0.2</version>
    </dependency>

`IterableJdbcTemplate` extends standard `JdbcTemplate` providing additional method `queryForIter(...)`
with many overloaded variants, covering all `queryForList(...)` and `query(...)` methods of `JdbcTemplate` that return `List`'s.

`IterableNamedParameterJdbcTemplate` extends standard `NamedParameterJdbcTemplate` also providing overloaded
`queryForIter(...)` method and `getIterableJdbcOperations()` method to access `IterableJdbcTemplate` (that is uses internally).

`IterableJdbcOperations` and `IterableNamedParameterJdbcOperations` interfaces are added to conform spring-jdbc style.

Usage example (contains spring transactions and injecting - they are not required):

    @Inject
    private IterableNamedParameterJdbcTemplate jt;

    @Transactional
    public void computeSomething() {
        CloseableIterator<Foo> fooIter = null;
        CloseableIterator<Bar> barIter = null;
        try {
            // open iterators
            fooIter = jt.queryForIter("select ...", fooParams, fooMapper);
            barIter = jt.queryForIter("select ...", barParams, barMapper);
            // do some processing with Iterators.transform(...) etc
            ...
        } finally {
            // release resources in case iterators wasn't exhausted
            // and closed automatically or exception happened
            IOUtils.closeQuietly(fooIter);
            IOUtils.closeQuietly(barIter);
        }
    }

How does it work
----------------

Proper JDBC resources releasing may be not easy (e.g. early connection releasing on error, resources may need unwrapping
to use proprietary extensions, but on releasing you must use wrapped ones etc). `JdbcTemplate` supports many environments
(`DriverManager`, connection pooling `JTA` etc) and has quite complex resource releasing process to support many cases with
different requirements.

`IterableJdbcTemplate` has the same query-execution code, as standard `JdbcTemplate`, but all resource-releasing code
was moved into `CloseableIterator#close()` method. So, within transaction bounds, it makes no difference whether to use
multiple iterators in the same method, or to use multiple `ResultSetExtractor`'s one inside the other (precisely speaking,
in `IterableJdbcTemplate` `SQLWarning`'s are checked after query execution but before results reading, when in `JdbcTemplate`
they are checked after all results are read before resource releasing; it may be fixed, but I think current variant is better).

License information
-------------------

This project is released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Changelog
---------

**1.0.2** (2013-03-13)

 * add `fetchSize` to constructors
 * make close checks atomic
 * `CloseableIterator` now extends `java.io.Closeable`

**1.0.1** (2012-11-09)

 * make `spring-jdbc` compile-scoped dependency

**1.0** (2012-11-08)

 * initial version
