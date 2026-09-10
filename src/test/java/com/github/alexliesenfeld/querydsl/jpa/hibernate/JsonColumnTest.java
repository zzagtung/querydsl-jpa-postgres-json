package com.github.alexliesenfeld.querydsl.jpa.hibernate;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.entity.TestEntity;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.support.SqlCapturingStatementInspector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;

/**
 * Exercises a column that really is of PostgreSQL type {@code json} rather than {@code jsonb}, so
 * the json half of every flavoured function is executed and not merely rendered.
 *
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 */
public class JsonColumnTest extends AbstractJsonPathTest {

    private JsonPath jsonPath;

    @BeforeEach
    void saveFixture() {
        TestEntity testEntity = newEntity();
        testEntity.setJsonChildParam(nestedSampleData());
        save(testEntity);
        // SqlTypes.JSON reads as jsonb from the annotation, so the json flavour has to be asked for.
        jsonPath = JsonPath.ofJson(q.jsonChildParam);
    }

    @Test
    public void typeOnAJsonColumn() {
        Assertions.assertEquals(1, count(jsonPath.type().eq("object")));
        Assertions.assertEquals(1, count(jsonPath.get("stringArray").type().eq("array")));
        Assertions.assertEquals(0, count(jsonPath.get("stringArray").type().eq("object")));
        Assertions.assertTrue(SqlCapturingStatementInspector.lastContaining("json_typeof(").isPresent(),
                () -> "no json_typeof in " + SqlCapturingStatementInspector.captured());
    }

    @Test
    public void lengthOnAJsonColumn() {
        Assertions.assertEquals(1, count(jsonPath.get("stringArray").length().eq(3)));
        Assertions.assertEquals(0, count(jsonPath.get("stringArray").length().eq(99)));
        // The non-array guard has to use the json flavour of typeof as well.
        Assertions.assertEquals(0, count(jsonPath.get("stringField").length().eq(1)));
        Assertions.assertTrue(SqlCapturingStatementInspector.lastContaining("json_array_length(").isPresent(),
                () -> "no json_array_length in " + SqlCapturingStatementInspector.captured());
    }

    @Test
    public void asTextOnAJsonColumn() {
        Assertions.assertEquals(1, count(jsonPath.get("stringField").asText().eq("6")));
        Assertions.assertEquals(0, count(jsonPath.get("stringField").asText().eq("7")));
    }

    @Test
    public void deepPathOnAJsonColumn() {
        Assertions.assertEquals(1,
                count(jsonPath.get("child").get("nested").get("fieldString").asText().eq("deep")));
        Assertions.assertEquals(0,
                count(jsonPath.get("nested").get("child").get("fieldString").asText().eq("deep")));
    }

    @Test
    public void lengthAndTypeProjectTheirJavaTypesOnAJsonColumn() {
        Assertions.assertEquals(3, selectFirst(jsonPath.get("stringArray").length()));
        Assertions.assertEquals("array", selectFirst(jsonPath.get("stringArray").type()));
    }

    /**
     * The annotation cannot distinguish json from jsonb, so JsonPath.of resolves to jsonb and emits
     * jsonb functions that PostgreSQL will not apply to a json column. This is why ofJson exists.
     */
    @Test
    public void ofResolvesToJsonbAndIsRejectedOnAJsonColumn() {
        Assertions.assertTrue(JsonPath.of(q.jsonChildParam).isJsonb());
        Assertions.assertThrows(DataAccessException.class,
                () -> count(JsonPath.of(q.jsonChildParam).type().eq("object")));
    }
}
