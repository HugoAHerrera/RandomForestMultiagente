package com.randomforest.randomforest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.HashMap;

public class SampleResultsTest {
    @Test
    void testEmptyConstructor() {
        SampleResults results = new SampleResults();
        assertNotNull(results.getSampleOutputs());
        assertTrue(results.getSampleOutputs().isEmpty());
    }

    @Test
    void testStoreSampleOutputs() {
        SampleResults results = new SampleResults();

        Map<String, Integer> sample1Results = new HashMap<>();
        sample1Results.put("Iris-setosa", 1);
        sample1Results.put("Iris-virginica", 2);

        results.getSampleOutputs().put("Muestra 1", sample1Results);

        assertEquals(1, ((Map<?, ?>)results.getSampleOutputs().get("Muestra 1")).get("Iris-setosa"));
        assertEquals(2, ((Map<?, ?>)results.getSampleOutputs().get("Muestra 1")).get("Iris-virginica"));
    }
}

