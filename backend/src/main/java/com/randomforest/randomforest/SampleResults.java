package com.randomforest.randomforest;

import java.util.LinkedHashMap;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SampleResults implements Serializable {
    private Map<String, Object> sampleOutputs;
    private double averageAccuracy;

    public SampleResults() {
        this.sampleOutputs = new LinkedHashMap<>();
    }
}

