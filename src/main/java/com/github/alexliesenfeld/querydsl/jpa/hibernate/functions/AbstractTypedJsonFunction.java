package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions;

import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;

import java.util.List;

/**
* Extracts a JSON value as text and casts it to a SQL type. The same flavour of {@code ->>} works
* for {@code json} and {@code jsonb}, so these functions do not depend on the JSON flavour.
*
* @author <a href=http://github.com/wenerme>wener</a>
* @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
* @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
*/
public abstract class AbstractTypedJsonFunction extends AbstractJsonSQLFunction {
    private final String conversion;

    protected AbstractTypedJsonFunction(String conversion, FunctionReturnTypeResolver returnTypeResolver) {
        super(returnTypeResolver);
        this.conversion = conversion;
    }

    @Override
    protected void doRender(SqlAppender sb, List<? extends SqlAstNode> arguments, ReturnableType<?> returnType,
                            SqlAstTranslator<?> walker) {
        if (conversion != null) {
            sb.append('(');
        }

        buildPath(sb, arguments, -1, walker);
        sb.append("->>");

        arguments.getLast().accept(walker);

        if (conversion != null) {
            sb.append(")::");
            sb.append(conversion);
        }
    }
}
