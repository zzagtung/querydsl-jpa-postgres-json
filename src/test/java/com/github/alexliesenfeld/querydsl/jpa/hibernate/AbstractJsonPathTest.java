package com.github.alexliesenfeld.querydsl.jpa.hibernate;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.dto.ChildSampleData;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.dto.SampleData;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.entity.QTestEntity;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.entity.TestEntity;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.initializer.Initializer;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.repository.TestRepository;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.support.SqlCapturingStatementInspector;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Shared Spring wiring and fixture helpers for the {@link JsonPath} integration tests. All
 * subclasses resolve to the same context configuration so the suite boots one context against one
 * shared PostgreSQL container.
 *
 * @author <a href=http://github.com/alexliesenfeld>Alexander Liesenfeld</a>
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {AbstractJsonPathTest.TestConfig.class}, initializers = {Initializer.class})
public abstract class AbstractJsonPathTest {

    @SpringBootApplication
    @ContextConfiguration
    static class TestConfig {}

    @Autowired
    protected TestRepository testRepository;

    @PersistenceContext
    protected EntityManager entityManager;

    protected final QTestEntity q = QTestEntity.testEntity;

    /** The captured statements are static, so drop anything the previous test left behind. */
    @BeforeEach
    void resetCapturedSql() {
        SqlCapturingStatementInspector.reset();
    }

    /** Number of rows matching {@code predicate}. */
    protected long count(Predicate predicate) {
        return StreamSupport.stream(testRepository.findAll(predicate).spliterator(), false).count();
    }

    /**
     * Projects {@code expression} for the first row. Declared as {@link Object} on purpose: it
     * keeps the compiler from inserting a cast, so assertions can observe the type Hibernate
     * actually returned.
     */
    protected Object selectFirst(Expression<?> expression) {
        return new JPAQuery<>(entityManager).select(expression).from(q).fetchFirst();
    }

    /** An entity with the non-JSON columns filled in and no JSON content yet. */
    protected TestEntity newEntity() {
        TestEntity testEntity = new TestEntity();
        testEntity.setFileName("filename");
        testEntity.setExtension("txt");
        testEntity.setLength(10L);
        return testEntity;
    }

    protected TestEntity save(TestEntity testEntity) {
        return testRepository.saveAndFlush(testEntity);
    }

    /**
     * A child three levels deep, so that key order in the rendered path is observable, carrying one
     * field per numeric cast.
     */
    protected SampleData nestedSampleData() {
        return SampleData.builder()
                .idField(6)
                .stringField("6")
                .child(ChildSampleData.builder()
                        .intField(7)
                        .integerField(8)
                        .longField(9L)
                        .longClsField(10L)
                        .shortField((short) 12)
                        .floatField(2.5f)
                        .doubleField(1.5d)
                        .boolField(true)
                        .fieldString("11")
                        .childArray(List.of("x", "y"))
                        .nested(ChildSampleData.builder()
                                .fieldString("deep")
                                .build())
                        .build())
                .stringArray(List.of("a", "b", "c"))
                .build();
    }
}
