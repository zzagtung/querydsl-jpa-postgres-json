package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.types;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.AbstractFlavouredJsonFunction;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.FixedReturnTypeResolver;
import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.type.descriptor.java.IntegerJavaType;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;
import org.hibernate.type.internal.NamedBasicTypeImpl;

import java.util.List;

/**
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class JsonArrayLengthSQLFunction extends AbstractFlavouredJsonFunction {

    private static final FunctionReturnTypeResolver arrayLength = new FixedReturnTypeResolver(
        new NamedBasicTypeImpl<>(IntegerJavaType.INSTANCE, IntegerJdbcType.INSTANCE, "array_length"));

    public JsonArrayLengthSQLFunction(boolean jsonb) {
        super(arrayLength, jsonb, ROOT_PATH_ARGUMENT_COUNT);
    }

    /**
     * {@code jsonb_array_length} raises {@code cannot get array length of a non-array} rather than
     * returning null, and a predicate is evaluated for every row, so one row holding an object
     * would abort the whole statement. Guarding on the type makes such a row evaluate to null,
     * which simply does not match.
     */
    @Override
    protected void doRender(SqlAppender sb, List<? extends SqlAstNode> arguments, ReturnableType<?> returnType,
                            SqlAstTranslator<?> walker) {
        sb.append("case when ");
        sb.append(flavoured("typeof"));
        sb.append('(');
        buildPath(sb, arguments, walker);
        sb.append(")='array' then ");
        sb.append(flavoured("array_length"));
        sb.append('(');
        buildPath(sb, arguments, walker);
        sb.append(") end");
    }
}
