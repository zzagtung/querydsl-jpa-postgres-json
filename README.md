# querydsl-jpa-postgres-json
This repository contains a Querydsl extension for working with JSON types when using JPA, Hibernate and PostgreSQL. It is based on https://github.com/wenerme/postjava but extends it to support more data types.

Java work with PostgreSQL

* Single class to rule both json and jsonb
* Hibernate json/jsonb dialect registry
* json/jsonb operator for QueryDSL
* json/jsonb function integration for QueryDSL

## Get started

### Install dialect

Each release targets one Hibernate minor version, because it builds on Hibernate SQL function SPIs
that change between them. Pick the row matching your Spring Boot version:

| Version | Spring Boot | Hibernate | Querydsl | Java |
|---------|-------------|-----------|----------|------|
| `0.3.0` | 3.4.5       | 6.6       | 6.12     | 21   |
| `0.2.0` | 3.2.3       | 6.4       | 6.1      | 21   |
| `0.1.1` | 3.2.0       | 6.3       | 5.2      | 17   |

```xml
<dependency>
    <groupId>io.github.zzagtung</groupId>
    <artifactId>querydsl-jpa-postgres-json</artifactId>
    <version>0.3.0</version>
</dependency>
```

```yaml
# Use the predefined dialect
spring.jpa.properties.hibernate.dialect: com.github.alexliesenfeld.querydsl.jpa.hibernate.PostgreSQLJsonDialect
```

Or derive your own dialect from it, calling `super.initializeFunctionRegistry` so the json/jsonb
functions stay registered:

```java
public class PostgreSQLCustomDialect extends PostgreSQLJsonDialect {

  @Override
  public void initializeFunctionRegistry(FunctionContributions functionContributions) {
    // Registers the json/jsonb functions
    super.initializeFunctionRegistry(functionContributions);
    // Add your own here
  }
}
```

### Define entity

```java
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "users")
@Setter
@Getter
class UserEntity {
    Integer id;
    @JdbcTypeCode(SqlTypes.JSON)
    JsonNode attributes;
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String, String> labels;
}
```

### Work with QueryDSL

```java
class PlayJson{
  public static void main(String[] args) {
    // Will auto detect json/jsonb
    JsonPath attrs = JsonPath.of(QUserEntity.userEntity.attributes);
    attrs.get("name").asText().like("wener%"); // String expression
    attrs.get("age").asInt().gt(18); // Integer expression
    attrs.get("score").asFloat().gt(1.5); // Float expression
    attrs.get("resources").contains(1); // Is array contains element
    attrs.get("resources").contains("A").not(); // Is array not contains element
    attrs.get("resources").length().gt(0); // Is array length > 0
    attrs.get("resources").type().eq("array"); // json type at this path
  }
}
```

Nested keys can be chained, or passed together:

```java
attrs.get("owner").get("address").get("city").asText().eq("Seoul");
attrs.get("owner", "address", "city").asText().eq("Seoul");
```

`asText`, `asInt`, `asLong`, `asShort`, `asFloat`, `asDouble` and `asBool` read a value at the path.
`type()` and `length()` map to `jsonb_typeof` and `jsonb_array_length`.

Notes on the two functions that depend on the JSON shape of a row:

* `length()` evaluates to `null` — matching nothing rather than failing the query — for a row where
  the path is absent or holds anything other than an array.
* `contains()` renders the PostgreSQL `@>` operator, which exists only for `jsonb`. On a `json` path
  it throws before any SQL is rendered.

`JsonPath.of(...)` picks json or jsonb from the column's `@JdbcTypeCode`. Use `JsonPath.ofJson(...)`
or `JsonPath.ofJsonb(...)` to choose explicitly, for example on a column declared through
`columnDefinition`.

## Upgrading to 0.3.0

Built and tested against Spring Boot 3.4.5 / Hibernate 6.6.13. Stay on `0.2.0` for Spring Boot 3.2.x.

Three fixes change query results rather than just failing louder, so check your usage:

* **Paths with three or more keys were rendered in the wrong order.** The intermediate keys came out
  back to front, so `a.get("b").get("c").get("d")` built `-> 'c' -> 'b' ->> 'd'` and such queries
  silently returned no rows. One and two key paths were unaffected. If you reordered your own keys to
  work around this, restore the natural order.
* **`type()` and `length()` did not work at all** and could not have been used before this release.
* **`contains()` on a `json` (non-jsonb) path** now throws instead of rendering invalid SQL.

# License
`querydsl-jpa-postgres-json` is free software: you can redistribute it and/or modify it under the terms of the MIT Public License.
 
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the MIT Public License for more details.
