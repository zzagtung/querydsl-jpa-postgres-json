package com.github.alexliesenfeld.querydsl.jpa.hibernate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildSampleData {
    private int intField;
    private Integer integerField;

    private long longField;
    private Long longClsField;

    private short shortField;
    private float floatField;
    private double doubleField;
    private boolean boolField;

    private String fieldString;

    private List<String> childArray;

    private ChildSampleData nested;
}
