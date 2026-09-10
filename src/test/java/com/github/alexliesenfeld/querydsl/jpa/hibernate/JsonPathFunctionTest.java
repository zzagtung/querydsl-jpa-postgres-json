package com.github.alexliesenfeld.querydsl.jpa.hibernate;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.entity.TestEntity;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.support.SqlCapturingStatementInspector;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Path construction, {@code jsonb_typeof} / {@code jsonb_array_length}, the json versus jsonb
 * distinction, and the edge cases around absent and quoted values.
 *
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 */
public class JsonPathFunctionTest extends AbstractJsonPathTest {

    private static final String QUOTED_VALUE = "it's a 'value'";

    /** One row carrying every JSON shape the assertions below need. */
    private void saveFixture() {
        TestEntity testEntity = newEntity();
        testEntity.setChildParam(nestedSampleData());
        testEntity.setEnumList(List.of(SampleEnum.TEST1, SampleEnum.TEST3));
        Map<String, String> tags = new LinkedHashMap<>();
        tags.put("key1", "val1");
        tags.put("quote", QUOTED_VALUE);
        testEntity.setTags(tags);
        save(testEntity);
    }

    /**
     * Regression for a reversed path: intermediate keys used to be emitted back to front, so
     * anything deeper than one intermediate key silently queried the wrong path.
     */
    @Test
    public void nestedPathKeepsKeyOrder() {
        saveFixture();
        JsonPath childParam = JsonPath.of(q.childParam);

        Assertions.assertEquals(1,
                count(childParam.get("child").get("nested").get("fieldString").asText().eq("deep")));
        // The same keys in the wrong order must not match.
        Assertions.assertEquals(0,
                count(childParam.get("nested").get("child").get("fieldString").asText().eq("deep")));
    }

    /**
     * The reversed-path bug is invisible with a single intermediate key, so each renderer needs a
     * path with two or more. This one drives {@code JsonFunction.doRender}, which passes the whole
     * argument list to buildPath.
     */
    @Test
    public void typeOnDeepPathKeepsKeyOrder() {
        saveFixture();
        JsonPath childParam = JsonPath.of(q.childParam);

        Assertions.assertEquals(1, count(childParam.get("child").get("nested").type().eq("object")));
        Assertions.assertEquals(0, count(childParam.get("nested").get("child").type().eq("object")));
    }

    /** Drives {@code JsonArrayLengthSQLFunction.doRender} with two intermediate keys. */
    @Test
    public void lengthOnDeepPathKeepsKeyOrder() {
        saveFixture();
        JsonPath childParam = JsonPath.of(q.childParam);

        Assertions.assertEquals(1, count(childParam.get("child").get("childArray").length().eq(2)));
        Assertions.assertEquals(0, count(childParam.get("childArray").get("child").length().eq(2)));
    }

    /**
     * Drives {@code JsonContainsSQLFunction.doRender}, whose buildPath range stops one short of the
     * argument list, with five arguments and three intermediate keys.
     */
    @Test
    public void containsOnDeepPathKeepsKeyOrder() {
        saveFixture();
        JsonPath childParam = JsonPath.of(q.childParam);

        Assertions.assertEquals(1, count(childParam.get("child").get("nested").get("fieldString").contains("deep")));
        Assertions.assertEquals(0, count(childParam.get("fieldString").get("nested").get("child").contains("deep")));
    }

    /**
     * jsonb_array_length raises on a non-array instead of returning null, so before the guard one
     * such row aborted the statement for every other row too.
     */
    @Test
    public void lengthIgnoresRowsWhereThePathIsNotAnArray() {
        TestEntity arrayRow = newEntity();
        arrayRow.setMixedTags(Map.of("value", List.of("a", "b", "c")));
        save(arrayRow);

        TestEntity scalarRow = newEntity();
        scalarRow.setMixedTags(Map.of("value", "scalar"));
        save(scalarRow);

        TestEntity objectRow = newEntity();
        objectRow.setMixedTags(Map.of("value", Map.of("k", "v")));
        save(objectRow);

        JsonPath mixedTags = JsonPath.of(q.mixedTags);
        // The scalar and object rows must simply not match rather than fail the query.
        Assertions.assertEquals(1, count(mixedTags.get("value").length().eq(3)));
        Assertions.assertEquals(0, count(mixedTags.get("value").length().eq(99)));
        // An object at the root of the path is the same situation one level up.
        Assertions.assertEquals(0, count(mixedTags.length().eq(1)));
    }

    @Test
    public void varargsGetBuildsTheSamePathAsChainedGet() {
        saveFixture();
        JsonPath childParam = JsonPath.of(q.childParam);

        Assertions.assertEquals(1, count(childParam.get("child", "nested", "fieldString").asText().eq("deep")));
        Assertions.assertEquals(0, count(childParam.get("child", "nested", "fieldString").asText().eq("shallow")));
    }

    @Test
    public void lengthOnRootPath() {
        saveFixture();
        JsonPath enumList = JsonPath.of(q.enumList);

        Assertions.assertEquals(1, count(enumList.length().eq(2)));
        Assertions.assertEquals(0, count(enumList.length().eq(99)));
    }

    @Test
    public void lengthOnKeyedPath() {
        saveFixture();
        JsonPath childParam = JsonPath.of(q.childParam);

        Assertions.assertEquals(1, count(childParam.get("stringArray").length().eq(3)));
        Assertions.assertEquals(0, count(childParam.get("stringArray").length().eq(99)));
    }

    @Test
    public void typeOnRootPath() {
        saveFixture();

        Assertions.assertEquals(1, count(JsonPath.of(q.enumList).type().eq("array")));
        Assertions.assertEquals(1, count(JsonPath.of(q.tags).type().eq("object")));
        Assertions.assertEquals(0, count(JsonPath.of(q.enumList).type().eq("object")));
    }

    @Test
    public void typeOnKeyedPath() {
        saveFixture();
        JsonPath childParam = JsonPath.of(q.childParam);

        Assertions.assertEquals(1, count(childParam.get("child").type().eq("object")));
        Assertions.assertEquals(1, count(childParam.get("stringArray").type().eq("array")));
        Assertions.assertEquals(1, count(childParam.get("stringField").type().eq("string")));
        Assertions.assertEquals(1, count(childParam.get("idField").type().eq("number")));
        Assertions.assertEquals(0, count(childParam.get("stringField").type().eq("number")));
    }

    @Test
    public void lengthAndTypeRenderJsonbFunctions() {
        saveFixture();
        SqlCapturingStatementInspector.reset();

        count(JsonPath.of(q.childParam).get("stringArray").length().eq(3));
        Assertions.assertTrue(
                SqlCapturingStatementInspector.lastContaining("jsonb_array_length(").isPresent(),
                () -> "no jsonb_array_length in " + SqlCapturingStatementInspector.captured());

        SqlCapturingStatementInspector.reset();
        count(JsonPath.of(q.enumList).type().eq("array"));
        Assertions.assertTrue(
                SqlCapturingStatementInspector.lastContaining("jsonb_typeof(").isPresent(),
                () -> "no jsonb_typeof in " + SqlCapturingStatementInspector.captured());
    }

    /**
     * A json path renders the json flavour of the function. The columns in this schema are jsonb,
     * so PostgreSQL rejects the statement -- which is exactly the evidence that the json and jsonb
     * registrations are no longer interchangeable.
     */
    @Test
    public void jsonPathRendersJsonFunctions() {
        SqlCapturingStatementInspector.reset();

        Assertions.assertThrows(DataAccessException.class,
                () -> count(JsonPath.ofJson(q.enumList).type().eq("array")));
        Assertions.assertTrue(
                SqlCapturingStatementInspector.lastContaining("json_typeof(").isPresent(),
                () -> "no json_typeof in " + SqlCapturingStatementInspector.captured());
    }

    /** PostgreSQL has no {@code @>} for json, so contains() must refuse a json path outright. */
    @Test
    public void containsRequiresJsonb() {
        Assertions.assertThrows(AssertionFailure.class,
                () -> JsonPath.ofJson(q.tags).get("key1").contains("val1"));
    }

    @Test
    public void explicitJsonbPathContains() {
        saveFixture();

        Assertions.assertEquals(1, count(JsonPath.ofJsonb(q.tags).get("key1").contains("val1")));
        Assertions.assertEquals(0, count(JsonPath.ofJsonb(q.tags).get("key1").contains("val2")));
    }

    @Test
    public void absentKeyYieldsNullText() {
        saveFixture();
        JsonPath tags = JsonPath.of(q.tags);

        Assertions.assertEquals(1, count(tags.get("absent").asText().isNull()));
        Assertions.assertEquals(0, count(tags.get("absent").asText().isNotNull()));
        Assertions.assertEquals(1, count(tags.get("key1").asText().isNotNull()));
    }

    @Test
    public void emptyMapIsAnObjectWithoutKeys() {
        TestEntity testEntity = newEntity();
        testEntity.setTags(new LinkedHashMap<>());
        save(testEntity);
        JsonPath tags = JsonPath.of(q.tags);

        Assertions.assertEquals(1, count(tags.type().eq("object")));
        Assertions.assertEquals(1, count(tags.get("key1").asText().isNull()));
        Assertions.assertEquals(0, count(tags.get("key1").asText().isNotNull()));
    }

    /** A single quote in a value survives, proving values are bound and not concatenated. */
    @Test
    public void valuesAreBoundAsParameters() {
        saveFixture();
        JsonPath tags = JsonPath.of(q.tags);

        Assertions.assertEquals(1, count(tags.get("quote").asText().eq(QUOTED_VALUE)));
        Assertions.assertEquals(1, count(tags.get("quote").asText().contains("'")));
        Assertions.assertEquals(1, count(tags.get("quote").contains(QUOTED_VALUE)));
        Assertions.assertEquals(0, count(tags.get("quote").asText().eq("its a value")));
    }
}
