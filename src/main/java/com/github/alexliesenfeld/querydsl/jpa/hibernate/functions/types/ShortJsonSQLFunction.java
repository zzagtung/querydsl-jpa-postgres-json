package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.types;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.AbstractTypedJsonFunction;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.FixedReturnTypeResolver;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.type.descriptor.java.ShortJavaType;
import org.hibernate.type.descriptor.jdbc.SmallIntJdbcType;
import org.hibernate.type.internal.NamedBasicTypeImpl;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class ShortJsonSQLFunction extends AbstractTypedJsonFunction {

    private static final FunctionReturnTypeResolver shortValue = new FixedReturnTypeResolver(
        new NamedBasicTypeImpl<>(ShortJavaType.INSTANCE, SmallIntJdbcType.INSTANCE, "smallint"));

    public ShortJsonSQLFunction() {
        super("smallint", shortValue);
    }
}
