package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.types;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.AbstractFlavouredJsonFunction;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.FixedReturnTypeResolver;
import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.descriptor.jdbc.BooleanJdbcType;
import org.hibernate.type.internal.NamedBasicTypeImpl;

import java.util.List;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class JsonContainsSQLFunction extends AbstractFlavouredJsonFunction {

  private static final FunctionReturnTypeResolver contains = new FixedReturnTypeResolver(
      new NamedBasicTypeImpl<>(BooleanJavaType.INSTANCE, BooleanJdbcType.INSTANCE, "contains"));

  public JsonContainsSQLFunction(boolean jsonb) {
    super(contains, jsonb);
  }

  @Override
  protected void doRender(SqlAppender sb, List<? extends SqlAstNode> arguments, ReturnableType<?> returnType,
                          SqlAstTranslator<?> walker) {

    buildPath(sb, arguments, -1, walker);
    sb.append("@>");
    arguments.getLast().accept(walker);
    sb.append("::");
    sb.append(isJsonb() ? "jsonb" : "json");
  }

}
