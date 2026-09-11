package com.github.alexliesenfeld.querydsl.jpa.hibernate.entity;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.SampleEnum;
import com.github.alexliesenfeld.querydsl.jpa.hibernate.dto.SampleData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID fileId;
    private String fileName;
    private String extension;
    private long length;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> tags;

    @JdbcTypeCode(SqlTypes.JSON)
    private SampleData childParam;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<SampleEnum> enumList;

    /** Lets one row hold an array and another a scalar under the same key. */
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> mixedTags;

    /**
     * SqlTypes.JSON maps to a jsonb column on PostgreSQL, so this is the only column that is really
     * of type json. JsonPath.isJsonb() reads the annotation and cannot tell the two apart, which is
     * why the json flavour has to be requested explicitly with JsonPath.ofJson.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private SampleData jsonChildParam;
}
