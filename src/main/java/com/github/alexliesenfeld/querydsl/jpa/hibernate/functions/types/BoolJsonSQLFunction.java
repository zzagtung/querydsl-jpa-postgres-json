package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.types;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.AbstractTypedJsonFunction;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.FixedReturnTypeResolver;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.descriptor.jdbc.BooleanJdbcType;
import org.hibernate.type.internal.NamedBasicTypeImpl;

/**
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class BoolJsonSQLFunction extends AbstractTypedJsonFunction {

    private static final FunctionReturnTypeResolver boolValue = new FixedReturnTypeResolver(
        new NamedBasicTypeImpl<>(BooleanJavaType.INSTANCE, BooleanJdbcType.INSTANCE, "boolean"));

    public BoolJsonSQLFunction() {
        super("boolean", boolValue);
    }
}
