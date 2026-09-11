package com.github.alexliesenfeld.querydsl.jpa.hibernate;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.dto.ChildSampleData;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.dto.SampleData;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.entity.TestEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One operator per test, each with a matching and a non-matching assertion, so that a failure names
 * the operator that broke.
 *
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 */
public class IntegrationTest extends AbstractJsonPathTest {

    @BeforeEach
    void saveFixtures() {
        TestEntity first = newEntity();
        first.setEnumList(List.of(SampleEnum.TEST1));
        first.setChildParam(SampleData.builder()
                .idField(0)
                .stringField("0")
                .child(ChildSampleData.builder()
                        .intField(1)
                        .integerField(2)
                        .longField(3L)
                        .longClsField(4L)
                        .fieldString("5")
                        .build())
                .stringArray(List.of("a", "b", "c"))
                .build());
        Map<String, String> tags = new LinkedHashMap<>();
        tags.put("key1", "val1");
        tags.put("key2", "val2");
        tags.put("key3", "val3");
        first.setTags(tags);
        save(first);

        TestEntity second = newEntity();
        second.setEnumList(List.of(SampleEnum.TEST1, SampleEnum.TEST3));
        second.setChildParam(SampleData.builder()
                .idField(6)
                .stringField("6")
                .child(ChildSampleData.builder()
                        .intField(7)
                        .integerField(8)
                        .longField(9L)
                        .longClsField(10L)
                        .fieldString("11")
                        .build())
                .stringArray(List.of("b", "c"))
                .build());
        tags = new LinkedHashMap<>();
        tags.put("key1", "val21");
        tags.put("key3", "val23");
        second.setTags(tags);
        save(second);
    }

    /** JsonPath's own contains, i.e. the jsonb {@code @>} operator, matches the whole value. */
    @Test
    public void tagValueContains() {
        JsonPath tags = JsonPath.of(q.tags);
        Assertions.assertEquals(1, count(tags.get("key1").contains("val21")));
        Assertions.assertEquals(0, count(tags.get("key1").contains("val2")));
    }

    /** Contains on the extracted text, i.e. {@code like '%val2%'}. */
    @Test
    public void tagTextContains() {
        JsonPath tags = JsonPath.of(q.tags);
        Assertions.assertEquals(1, count(tags.get("key1").asText().contains("val2")));
        Assertions.assertEquals(2, count(tags.get("key1").asText().contains("val")));
        Assertions.assertEquals(0, count(tags.get("key1").asText().contains("nope")));
    }

    @Test
    public void tagTextLike() {
        JsonPath tags = JsonPath.of(q.tags);
        Assertions.assertEquals(1, count(tags.get("key1").asText().like("val2%")));
        Assertions.assertEquals(0, count(tags.get("key1").asText().like("nope%")));
    }

    @Test
    public void tagTextEquals() {
        JsonPath tags = JsonPath.of(q.tags);
        Assertions.assertEquals(1, count(tags.get("key1").asText().eq("val21")));
        Assertions.assertEquals(0, count(tags.get("key1").asText().eq("val2")));
    }

    @Test
    public void childLongComparison() {
        JsonPath childParam = JsonPath.of(q.childParam);
        Assertions.assertEquals(1, count(childParam.get("child").get("longField").asLong().goe(9L)));
        Assertions.assertEquals(0, count(childParam.get("child").get("longField").asLong().goe(100L)));
    }

    @Test
    public void stringArrayContains() {
        JsonPath childParam = JsonPath.of(q.childParam);
        Assertions.assertEquals(2, count(childParam.get("stringArray").contains("b")));
        Assertions.assertEquals(1, count(childParam.get("stringArray").contains("a")));
        Assertions.assertEquals(0, count(childParam.get("stringArray").contains("z")));
    }

    @Test
    public void enumListContains() {
        JsonPath enumList = JsonPath.of(q.enumList);
        Assertions.assertEquals(2, count(enumList.contains(SampleEnum.TEST1)));
        Assertions.assertEquals(1, count(enumList.contains(SampleEnum.TEST3)));
        Assertions.assertEquals(0, count(enumList.contains(SampleEnum.TEST2)));
    }

    /** Predicates on JSON and on ordinary columns combine. */
    @Test
    public void jsonPredicateCombinesWithColumnPredicate() {
        JsonPath tags = JsonPath.of(q.tags);
        Assertions.assertEquals(1, count(q.fileName.eq("filename").and(tags.get("key1").asText().eq("val21"))));
        Assertions.assertEquals(0, count(q.fileName.eq("other").and(tags.get("key1").asText().eq("val21"))));
    }
}
