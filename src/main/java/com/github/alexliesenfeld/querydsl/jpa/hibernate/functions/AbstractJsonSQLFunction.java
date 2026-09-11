package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions;

import org.hibernate.AssertionFailure;
import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.ColumnReference;
import org.hibernate.sql.ast.tree.update.Assignable;

import java.util.List;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public abstract class AbstractJsonSQLFunction
        extends AbstractSqmSelfRenderingFunctionDescriptor {

  /**
   * Functions that dereference a JSON key ({@code ->>}, {@code @>}) need the path plus at least one
   * key, so they require two arguments.
   */
  protected static final int MINIMAL_ARGUMENT_COUNT = 2;
  /**
   * Functions that only wrap a path ({@code jsonb_typeof}, {@code jsonb_array_length}) are also
   * meaningful on the root path, which contributes a single argument.
   */
  protected static final int ROOT_PATH_ARGUMENT_COUNT = 1;
  protected static final int MAXIMAL_ARGUMENT_COUNT = 64;


  public void buildPath(SqlAppender sb,  List<? extends SqlAstNode>  arguments, SqlAstTranslator<?> walker) {
    buildPath(sb, arguments, 0, arguments.size(), walker);
  }

  public void buildPath(SqlAppender sb, List<? extends SqlAstNode>  arguments, int n, SqlAstTranslator<?> walker) {
    buildPath(sb, arguments, 0, n < 0 ? arguments.size() + n : n, walker);
  }

  protected void buildPath(SqlAppender sb, List<? extends SqlAstNode>  arguments, int from, int to, SqlAstTranslator<?> walker) {
    Object arg = arguments.get(from);

    if (!(arg instanceof Assignable))
      throw new AssertionFailure("Not assignable");

    ColumnReference columnReference = ((Assignable) arg).getColumnReferences().getFirst();
    sb.append(columnReference.getExpressionText());

    for (int i = from + 1; i < to; i++) {
      sb.append("->");
      arguments.get(i).accept(walker);
    }
  }

  protected AbstractJsonSQLFunction(FunctionReturnTypeResolver returnTypeResolver) {
    this(returnTypeResolver, MINIMAL_ARGUMENT_COUNT);
  }

  protected AbstractJsonSQLFunction(FunctionReturnTypeResolver returnTypeResolver, int minimumArgumentCount) {
    super(
        "sql",
        StandardArgumentsValidators.between(minimumArgumentCount, MAXIMAL_ARGUMENT_COUNT),
        returnTypeResolver,
        null
    );
  }

  public void render(
          SqlAppender sqlAppender,
          List<? extends SqlAstNode> sqlAstArguments,
          ReturnableType<?> returnType,
          SqlAstTranslator<?> walker) {
    doRender(sqlAppender, sqlAstArguments, returnType, walker);
  }

  protected abstract void doRender(SqlAppender sb, List<? extends SqlAstNode> arguments, ReturnableType<?> returnType, SqlAstTranslator<?> walker);

  @Override
  public String getArgumentListSignature() {
    return "";
  }
}
