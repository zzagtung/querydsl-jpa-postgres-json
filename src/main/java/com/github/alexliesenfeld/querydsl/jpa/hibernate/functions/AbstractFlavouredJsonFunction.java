package com.github.alexliesenfeld.querydsl.jpa.hibernate.functions;

import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;

/**
 * Base class for the functions that render differently for {@code json} and {@code jsonb}. The
 * flavour is fixed at construction: a descriptor registered in the {@code SqmFunctionRegistry} is
 * shared by every session of a {@code SessionFactory}, so it must not be mutable.
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public abstract class AbstractFlavouredJsonFunction extends AbstractJsonSQLFunction {

  private final boolean jsonb;

  protected AbstractFlavouredJsonFunction(FunctionReturnTypeResolver returnTypeResolver, boolean jsonb) {
    super(returnTypeResolver);
    this.jsonb = jsonb;
  }

  protected AbstractFlavouredJsonFunction(FunctionReturnTypeResolver returnTypeResolver, boolean jsonb,
                                          int minimumArgumentCount) {
    super(returnTypeResolver, minimumArgumentCount);
    this.jsonb = jsonb;
  }

  public boolean isJsonb() {
    return jsonb;
  }

  /** Prefixes a PostgreSQL function name with the JSON flavour this descriptor renders for. */
  protected String flavoured(String functionName) {
    return (jsonb ? "jsonb_" : "json_") + functionName;
  }
}
