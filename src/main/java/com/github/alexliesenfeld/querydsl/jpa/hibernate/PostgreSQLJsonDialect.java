package com.github.alexliesenfeld.querydsl.jpa.hibernate;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.functions.types.*;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.PostgresPlusDialect;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 * @see <a href=https://www.postgresql.org/docs/current/static/functions-json.html>functions-json</a>
 */
public class PostgreSQLJsonDialect extends PostgresPlusDialect {

  /**
   * You can either directly use it or derive your custom dialect by using this constructor.
   */
  public PostgreSQLJsonDialect() {
    super();
  }


  @Override
  public void initializeFunctionRegistry(FunctionContributions functionContributions) {
    super.initializeFunctionRegistry(functionContributions);

    functionContributions.getFunctionRegistry().register("hql_json_text", new TextJsonSQLFunction());
    functionContributions.getFunctionRegistry().register("hql_json_int", new IntJsonSQLFunction());
    functionContributions.getFunctionRegistry().register("hql_json_float", new FloatJsonSQLFunction());
    functionContributions.getFunctionRegistry().register("hql_json_double", new DoubleJsonSQLFunction());
    functionContributions.getFunctionRegistry().register("hql_json_long", new LongJsonSQLFunction());
    functionContributions.getFunctionRegistry().register("hql_json_short", new ShortJsonSQLFunction());
    functionContributions.getFunctionRegistry().register("hql_json_bool", new BoolJsonSQLFunction());

    functionContributions.getFunctionRegistry().register("hql_json_" + "array_length", new JsonArrayLengthSQLFunction(false));
    functionContributions.getFunctionRegistry().register("hql_jsonb_" + "array_length", new JsonArrayLengthSQLFunction(true));

    functionContributions.getFunctionRegistry().register("hql_json_" + "typeof", new JsonTypeofSQLFunction(false));
    functionContributions.getFunctionRegistry().register("hql_jsonb_" + "typeof", new JsonTypeofSQLFunction(true));

    // PostgreSQL only offers @> for jsonb, so there is no hql_json_contains counterpart. JsonPath
    // enforces this by rejecting contains() on a json path before any SQL is rendered.
    functionContributions.getFunctionRegistry().register("hql_jsonb_contains", new JsonContainsSQLFunction(true));

  }

}
