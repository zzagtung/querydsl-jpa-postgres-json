package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.types;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.AbstractTypedJsonFunction;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.FixedReturnTypeResolver;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.type.descriptor.java.IntegerJavaType;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;
import org.hibernate.type.internal.NamedBasicTypeImpl;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class IntJsonSQLFunction extends AbstractTypedJsonFunction {

    private static final FunctionReturnTypeResolver intValue = new FixedReturnTypeResolver(
        new NamedBasicTypeImpl<>(IntegerJavaType.INSTANCE, IntegerJdbcType.INSTANCE, "int"));

    public IntJsonSQLFunction() {
        super("int", intValue);
    }
}
