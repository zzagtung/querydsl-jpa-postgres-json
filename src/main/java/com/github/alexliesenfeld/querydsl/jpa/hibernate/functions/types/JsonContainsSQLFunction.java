package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.types;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.AbstractJsonSQLFunction;
import org.hibernate.metamodel.mapping.BasicValuedMapping;
import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.query.sqm.tree.SqmTypedNode;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.type.BasicType;
import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.descriptor.jdbc.BooleanJdbcType;
import org.hibernate.type.internal.NamedBasicTypeImpl;
import org.hibernate.type.spi.TypeConfiguration;

import java.util.List;
import java.util.function.Supplier;

import static org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers.useImpliedTypeIfPossible;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class JsonContainsSQLFunction extends AbstractJsonSQLFunction {

  private static final FunctionReturnTypeResolver jsonContainsResult = new FunctionReturnTypeResolver() {

    private final BasicType<Boolean> contains =
        new NamedBasicTypeImpl<>(BooleanJavaType.INSTANCE, BooleanJdbcType.INSTANCE, "contains");

    @Override
    public ReturnableType<?> resolveFunctionReturnType(ReturnableType<?> impliedType,
                                                       List<? extends SqmTypedNode<?>> arguments,
                                                       TypeConfiguration typeConfiguration) {
      // json contains result is boolean.
      return contains;
    }

    @Override
    public BasicValuedMapping resolveFunctionReturnType(Supplier<BasicValuedMapping> impliedTypeAccess, List<? extends SqlAstNode> arguments) {
      return useImpliedTypeIfPossible(contains, impliedTypeAccess.get());
    }

    @Override
    public String getReturnType() {
      return contains.getJavaType().getSimpleName();
    }
  };

  public JsonContainsSQLFunction() {
    super(jsonContainsResult);
  }

  @Override
  protected void doRender(SqlAppender sb, List<? extends SqlAstNode> arguments, SqlAstTranslator<?> walker) {

    super.buildPath(sb, arguments, -1, walker);
    sb.append("@>");
    arguments.getLast().accept(walker);
    sb.append("::");
    sb.append(isJsonb() ? "jsonb" : "json");
  }

}
