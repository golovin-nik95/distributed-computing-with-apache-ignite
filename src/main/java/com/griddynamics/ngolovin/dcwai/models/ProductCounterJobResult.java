package com.griddynamics.ngolovin.dcwai.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCounterJobResult {

    private String nodeId;
    private Map<String, Long> productsByPriceRanges;
    private Long executionTimeMillis;
}
