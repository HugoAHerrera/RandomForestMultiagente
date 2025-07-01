package com.randomforest.randomforest;

import java.util.LinkedHashMap;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SampleResults implements Serializable {
    /**
     * Map of the prediction results
     * For example {"sepal_length": 1}
     */
    private Map<String, Object> sampleOutputs;

    /**
     * Accuracy value.
     */
    private double averageAccuracy;

    /**
     * Empty constructor that initializes the attribute sampleOutputs
     */
    public SampleResults() {
        this.sampleOutputs = new LinkedHashMap<>();
    }
}

