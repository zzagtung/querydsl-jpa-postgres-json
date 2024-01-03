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
public class SampleData {
    private int idField;
    private String stringField;
    private ChildSampleData child;
    private List<String> stringArray;
}
