package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions;

import org.hibernate.metamodel.mapping.BasicValuedMapping;
import org.hibernate.metamodel.mapping.MappingModelExpressible;
import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.query.sqm.tree.SqmTypedNode;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.type.BasicType;
import org.hibernate.type.spi.TypeConfiguration;

import java.util.List;
import java.util.function.Supplier;

import static org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers.useImpliedTypeIfPossible;

/**
 * Resolves to a fixed {@link BasicType}, regardless of the arguments a function is called with.
 *
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class FixedReturnTypeResolver implements FunctionReturnTypeResolver {

  private final BasicType<?> type;

  public FixedReturnTypeResolver(BasicType<?> type) {
    this.type = type;
  }

  @Override
  public ReturnableType<?> resolveFunctionReturnType(ReturnableType<?> impliedType,
                                                     Supplier<MappingModelExpressible<?>> inferredTypeSupplier,
                                                     List<? extends SqmTypedNode<?>> arguments,
                                                     TypeConfiguration typeConfiguration) {
    return type;
  }

  @Override
  public BasicValuedMapping resolveFunctionReturnType(Supplier<BasicValuedMapping> impliedTypeAccess,
                                                      List<? extends SqlAstNode> arguments) {
    return useImpliedTypeIfPossible(type, impliedTypeAccess.get());
  }

  @Override
  public String getReturnType() {
    return type.getJavaType().getSimpleName();
  }
}
