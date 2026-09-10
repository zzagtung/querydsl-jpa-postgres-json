package com.github.alexliesenfeld.querydsl.jpa.hibernate;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.entity.TestEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Selects the JSON expressions instead of only filtering on them. A predicate barely exercises the
 * resolved return type, so this is what actually pins the FunctionReturnTypeResolver of every
 * descriptor: the value has to come back as the Java type {@link JsonPath} promises.
 *
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 */
public class JsonPathProjectionTest extends AbstractJsonPathTest {

    private JsonPath childParam;
    private JsonPath child;

    @BeforeEach
    void saveFixture() {
        TestEntity testEntity = newEntity();
        testEntity.setChildParam(nestedSampleData());
        save(testEntity);
        childParam = JsonPath.of(q.childParam);
        child = childParam.get("child");
    }

    @Test
    public void lengthProjectsAnInteger() {
        Object length = selectFirst(childParam.get("stringArray").length());

        Assertions.assertEquals(Integer.class, length.getClass());
        Assertions.assertEquals(3, length);
    }

    @Test
    public void typeProjectsAString() {
        Object type = selectFirst(childParam.get("stringArray").type());

        Assertions.assertEquals(String.class, type.getClass());
        Assertions.assertEquals("array", type);
    }

    /** A non-array path projects null rather than raising. */
    @Test
    public void lengthProjectsNullForANonArrayPath() {
        Assertions.assertNull(selectFirst(childParam.get("stringField").length()));
    }

    /**
     * The Java type a projection yields happens to match what the driver returns for these SQL
     * types either way, so what really pins the declared return type is feeding the result to
     * something that demands that type: {@code lower()} needs a string.
     */
    @Test
    public void typeIsUsableAsAString() {
        Assertions.assertEquals(1, count(childParam.get("stringArray").type().lower().eq("array")));
        Assertions.assertEquals(0, count(childParam.get("stringArray").type().lower().eq("object")));
    }

    /** Likewise for array_length, which has to be declared numeric to reach {@code abs()}. */
    @Test
    public void lengthIsUsableAsANumber() {
        Assertions.assertEquals(1, count(childParam.get("stringArray").length().abs().eq(3)));
        Assertions.assertEquals(1, count(childParam.get("stringArray").length().add(1).eq(4)));
        Assertions.assertEquals(0, count(childParam.get("stringArray").length().add(1).eq(3)));
    }

    /** Same check for the casts: each has to be declared numeric to reach {@code abs()}. */
    @Test
    public void numericCastsAreUsableAsNumbers() {
        Assertions.assertEquals(1, count(child.get("intField").asInt().abs().eq(7)));
        Assertions.assertEquals(1, count(child.get("longField").asLong().abs().eq(9L)));
        Assertions.assertEquals(1, count(child.get("shortField").asShort().abs().eq((short) 12)));
        Assertions.assertEquals(1, count(child.get("floatField").asFloat().abs().eq(2.5f)));
        Assertions.assertEquals(1, count(child.get("doubleField").asDouble().abs().eq(1.5d)));
    }

    @Test
    public void asTextIsUsableAsAString() {
        Assertions.assertEquals(1, count(child.get("fieldString").asText().lower().eq("11")));
        Assertions.assertEquals(0, count(child.get("fieldString").asText().lower().eq("12")));
    }

    @Test
    public void asTextProjectsAString() {
        Object value = selectFirst(child.get("fieldString").asText());

        Assertions.assertEquals(String.class, value.getClass());
        Assertions.assertEquals("11", value);
    }

    @Test
    public void asIntProjectsAnInteger() {
        Object value = selectFirst(child.get("intField").asInt());

        Assertions.assertEquals(Integer.class, value.getClass());
        Assertions.assertEquals(7, value);
    }

    @Test
    public void asLongProjectsALong() {
        Object value = selectFirst(child.get("longField").asLong());

        Assertions.assertEquals(Long.class, value.getClass());
        Assertions.assertEquals(9L, value);
    }

    @Test
    public void asShortProjectsAShort() {
        Object value = selectFirst(child.get("shortField").asShort());

        Assertions.assertEquals(Short.class, value.getClass());
        Assertions.assertEquals((short) 12, value);
    }

    @Test
    public void asFloatProjectsAFloat() {
        Object value = selectFirst(child.get("floatField").asFloat());

        Assertions.assertEquals(Float.class, value.getClass());
        Assertions.assertEquals(2.5f, value);
    }

    @Test
    public void asDoubleProjectsADouble() {
        Object value = selectFirst(child.get("doubleField").asDouble());

        Assertions.assertEquals(Double.class, value.getClass());
        Assertions.assertEquals(1.5d, value);
    }

    @Test
    public void asBoolProjectsABoolean() {
        Object value = selectFirst(child.get("boolField").asBool());

        Assertions.assertEquals(Boolean.class, value.getClass());
        Assertions.assertEquals(true, value);
    }
}
