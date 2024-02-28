package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.query.ReturnableType;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.type.Type;

import java.util.List;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
@Setter
@Getter
public class JsonFunction extends AbstractJsonSQLFunction {
  protected String functionName;
  protected String jsonbFunctionName;
  protected String jsonFunctionName;
  protected Type type;

  JsonFunction() {
    super();
  }

  @Override
  protected void doRender(SqlAppender sb, List<? extends SqlAstNode> arguments, ReturnableType<?> returnType,
                          SqlAstTranslator<?> walker) {
    sb.append(isJsonb() ? jsonbFunctionName : jsonFunctionName);
    sb.append('(');
    buildPath(sb, arguments, walker);
    sb.append(')');
  }

}
