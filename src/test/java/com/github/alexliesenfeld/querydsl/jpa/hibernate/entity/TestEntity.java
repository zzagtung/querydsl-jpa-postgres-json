package com.github.alexliesenfeld.querydsl.jpa.hibernate.entity;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.EnumTest;
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
    private List<EnumTest> enumList;
}