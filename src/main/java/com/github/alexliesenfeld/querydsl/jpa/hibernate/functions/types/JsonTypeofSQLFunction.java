package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.types;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.FixedReturnTypeResolver;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.JsonFunction;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.type.descriptor.java.StringJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.hibernate.type.internal.NamedBasicTypeImpl;

/**
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class JsonTypeofSQLFunction extends JsonFunction {

    private static final FunctionReturnTypeResolver typeof = new FixedReturnTypeResolver(
        new NamedBasicTypeImpl<>(StringJavaType.INSTANCE, VarcharJdbcType.INSTANCE, "typeof"));

    public JsonTypeofSQLFunction(boolean jsonb) {
        super(typeof, "typeof", jsonb);
    }
}
