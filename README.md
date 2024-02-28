# querydsl-jpa-postgres-json
This repository contains a Querydsl extension for working with JSON types when using JPA, Hibernate and PostgreSQL. It is based on https://github.com/wenerme/postjava but extends it to support more data types.

Java work with PostgreSQL

* Single class to rule both json and jsonb
* Hibernate json/jsonb dialect registry
* json/jsonb operator for QueryDSL
* json/jsonb function integration for QueryDSL

## Get started

### Install dialect

```xml
<!-- spring boot 3.2.0, hibernate 6.3, querydsl 5.2 -->
<dependency>
    <groupId>io.github.zzagtung</groupId>
    <artifactId>querydsl-jpa-postgres-json</artifactId>
    <version>0.1.1</version>
</dependency>

<!-- spring boot 3.2.3, hibernate 6.4, querydsl 6.1 -->
<dependency>
    <groupId>io.github.zzagtung</groupId>
    <artifactId>querydsl-jpa-postgres-json</artifactId>
    <version>0.2.0</version>
</dependency>
```


```yaml
# Use the predefined dialect
spring.jpa.properties.hibernate.dialect: com.github.alexliesenfeld.querydsl.jpa.hibernate.PostgreSQLJsonDialect
```

Or use your customized dialect

```java
public class PostgreSQLCustomDialect extends PostgreSQLDialect {

  public PostgreSQLCustomDialect() {
    super();
    // Add json/jsonb support
    PostgreSQLJsonDialect.register(this);
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
    attrs.get("resources").contain(1).isTrue(); // Is array contain element 
    attrs.get("resources").contain("A").isTrue().not(); // Is array not contain element 
    attrs.get("resources").contain("A").isFalse(); // Is array not contain element
    attrs.get("resources").length().gt(0); // Is array length > 0
  }
}
```

# License
`querydsl-jpa-postgres-json` is free software: you can redistribute it and/or modify it under the terms of the MIT Public License.
 
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the MIT Public License for more details.
