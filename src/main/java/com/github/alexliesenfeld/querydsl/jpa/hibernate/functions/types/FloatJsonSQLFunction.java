package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.types;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.AbstractTypedJsonFunction;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.FixedReturnTypeResolver;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.type.descriptor.java.FloatJavaType;
import org.hibernate.type.descriptor.jdbc.FloatJdbcType;
import org.hibernate.type.internal.NamedBasicTypeImpl;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class FloatJsonSQLFunction extends AbstractTypedJsonFunction {

    private static final FunctionReturnTypeResolver floatValue = new FixedReturnTypeResolver(
        new NamedBasicTypeImpl<>(FloatJavaType.INSTANCE, FloatJdbcType.INSTANCE, "float4"));

    public FloatJsonSQLFunction() {
        super("float4", floatValue);
    }
}
