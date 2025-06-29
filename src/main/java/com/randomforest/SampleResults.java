package com.randomforest;

import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SampleResults {
    private Map<String, Object> sampleOutputs;
    private double averageAccuracy;

    public SampleResults() {
        this.sampleOutputs = new LinkedHashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (Map.Entry<String, Object> entry : sampleOutputs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Double) {
                sb.append(String.format(java.util.Locale.US, "  \"%s\": %.2f,%n", key, (Double) value));
            } else {
                sb.append("  \"" + key + "\": " + value + ",\n");
            }
        }
        sb.append(String.format(java.util.Locale.US, "  \"Accuracy\": \"%.2f%%\"%n", averageAccuracy));
        sb.append("}");
        return sb.toString();
    }
}

