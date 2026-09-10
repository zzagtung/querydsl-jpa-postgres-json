package com.github.alexliesenfeld.querydsl.jpa.hibernate.support;

import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Records every statement Hibernate sends so tests can assert on the rendered JSON SQL rather than
 * only on query results. Hibernate instantiates this itself, so it needs a public no-arg
 * constructor and has to keep its state static.
 */
public class SqlCapturingStatementInspector implements StatementInspector {

    private static final List<String> STATEMENTS = Collections.synchronizedList(new ArrayList<>());

    @Override
    public String inspect(String sql) {
        STATEMENTS.add(sql);
        return sql;
    }

    public static void reset() {
        STATEMENTS.clear();
    }

    /** The most recently captured statement containing {@code fragment}, if any. */
    public static Optional<String> lastContaining(String fragment) {
        synchronized (STATEMENTS) {
            return STATEMENTS.stream().filter(sql -> sql.contains(fragment)).reduce((first, second) -> second);
        }
    }

    /** All captured statements, for failure messages. */
    public static List<String> captured() {
        synchronized (STATEMENTS) {
            return List.copyOf(STATEMENTS);
        }
    }
}
