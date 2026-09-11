package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions;

import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;

import java.util.List;

/**
 * Renders a PostgreSQL JSON function that takes a whole path as its single operand, such as
 * {@code jsonb_typeof(tags->'key')}. Because the path itself is the operand, these functions are
 * also meaningful on the root path and therefore accept a single argument.
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class JsonFunction extends AbstractFlavouredJsonFunction {

  protected final String functionName;

  protected JsonFunction(FunctionReturnTypeResolver returnTypeResolver, String functionName, boolean jsonb) {
    super(returnTypeResolver, jsonb, ROOT_PATH_ARGUMENT_COUNT);
    this.functionName = functionName;
  }

  @Override
  protected void doRender(SqlAppender sb, List<? extends SqlAstNode> arguments, ReturnableType<?> returnType,
                          SqlAstTranslator<?> walker) {
    sb.append(flavoured(functionName));
    sb.append('(');
    buildPath(sb, arguments, walker);
    sb.append(')');
  }

}
