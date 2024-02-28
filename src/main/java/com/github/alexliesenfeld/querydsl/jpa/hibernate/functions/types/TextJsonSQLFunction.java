package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.types;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.AbstractTypedJsonFunction;
import org.hibernate.metamodel.mapping.BasicValuedMapping;
import org.hibernate.metamodel.mapping.MappingModelExpressible;
import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.query.sqm.tree.SqmTypedNode;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.type.BasicType;
import org.hibernate.type.descriptor.java.StringJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.hibernate.type.internal.NamedBasicTypeImpl;
import org.hibernate.type.spi.TypeConfiguration;

import java.util.List;
import java.util.function.Supplier;

import static org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers.useImpliedTypeIfPossible;

/**
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class TextJsonSQLFunction extends AbstractTypedJsonFunction {

    private static final FunctionReturnTypeResolver textValContains = new FunctionReturnTypeResolver() {

        private final BasicType<String> contains =
            new NamedBasicTypeImpl<>(StringJavaType.INSTANCE, VarcharJdbcType.INSTANCE, "contains");

        @Override
        public ReturnableType<?> resolveFunctionReturnType(ReturnableType<?> impliedType,
                                                           Supplier<MappingModelExpressible<?>> inferredTypeSupplier,
                                                           List<? extends SqmTypedNode<?>> arguments,
                                                           TypeConfiguration typeConfiguration) {
            // text contains or like
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

    public TextJsonSQLFunction() {
        super("text", textValContains);
    }
}
