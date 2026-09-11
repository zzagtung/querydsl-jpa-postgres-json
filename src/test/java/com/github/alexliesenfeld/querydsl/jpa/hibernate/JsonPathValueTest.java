package com.github.alexliesenfeld.querydsl.jpa.hibernate;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.entity.TestEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Coverage for every {@code as*} cast JsonPath exposes. The single fixture row makes the negative
 * assertions meaningful: a cast that silently matched everything would return 1 instead of 0.
 *
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 */
public class JsonPathValueTest extends AbstractJsonPathTest {

    private JsonPath child;

    @BeforeEach
    void saveFixture() {
        TestEntity testEntity = newEntity();
        testEntity.setChildParam(nestedSampleData());
        save(testEntity);
        child = JsonPath.of(q.childParam).get("child");
    }

    @Test
    public void asText() {
        Assertions.assertEquals(1, count(child.get("fieldString").asText().eq("11")));
        Assertions.assertEquals(0, count(child.get("fieldString").asText().eq("12")));
    }

    @Test
    public void asInt() {
        Assertions.assertEquals(1, count(child.get("intField").asInt().eq(7)));
        Assertions.assertEquals(0, count(child.get("intField").asInt().eq(8)));
    }

    @Test
    public void asShort() {
        Assertions.assertEquals(1, count(child.get("shortField").asShort().eq((short) 12)));
        Assertions.assertEquals(0, count(child.get("shortField").asShort().eq((short) 13)));
    }

    @Test
    public void asLong() {
        Assertions.assertEquals(1, count(child.get("longField").asLong().eq(9L)));
        Assertions.assertEquals(0, count(child.get("longField").asLong().eq(99L)));
    }

    @Test
    public void asFloat() {
        Assertions.assertEquals(1, count(child.get("floatField").asFloat().eq(2.5f)));
        Assertions.assertEquals(0, count(child.get("floatField").asFloat().eq(3.5f)));
    }

    @Test
    public void asDouble() {
        Assertions.assertEquals(1, count(child.get("doubleField").asDouble().eq(1.5d)));
        Assertions.assertEquals(0, count(child.get("doubleField").asDouble().eq(2.5d)));
    }

    @Test
    public void asBool() {
        Assertions.assertEquals(1, count(child.get("boolField").asBool().isTrue()));
        Assertions.assertEquals(0, count(child.get("boolField").asBool().isFalse()));
    }

    /** Numeric casts still compare numerically, not lexicographically. */
    @Test
    public void asLongComparesNumerically() {
        Assertions.assertEquals(1, count(child.get("longClsField").asLong().gt(9L)));
        Assertions.assertEquals(0, count(child.get("longClsField").asLong().lt(9L)));
    }
}
