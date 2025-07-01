package com.randomforest.randomforest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class MetricTest {
    @Test
    void testCalculateEntropy() {
        List<Object> data = List.of("M", "F", "M", "F", "M");

        double expectedEntropy = 0.9709505944546686;
        double entropy = Metric.calculateEntropy(data);

        assertEquals(expectedEntropy, entropy);
    }

    @Test
    void testCalculateEntropyPure() {
        List<Object> data = List.of("M", "M", "M");

        double expectedEntropy = 0;
        double entropy = Metric.calculateEntropy(data);

        assertEquals(expectedEntropy, entropy);
    }

    @Test
    void testCalculateEntropy3Types() {
        List<Object> data = List.of("M", "X", "F", "M", "X", "F", "M", "X");

        double expectedEntropy = 1.5612781244591325;
        double entropy = Metric.calculateEntropy(data);

        assertEquals(expectedEntropy, entropy);
    }

    @Test
    void testCalculateMSE() {
        List<Object> data = List.of(2.0, 4.0, 6.0, 8.0);

        double expectedMSE = 5.0;
        double mse = Metric.calculateMSE(data);

        assertEquals(expectedMSE, mse);
    }

    @Test
    void testCalculateMSEAllEqual() {
        List<Object> data = List.of(3.0, 3.0, 3.0);

        double expectedMSE = 0.0;
        double mse = Metric.calculateMSE(data);

        assertEquals(expectedMSE, mse);
    }

    @Test
    void testCalculateMSEWithIntegers() {
        List<Object> data = List.of(1, 2, 3, 4);

        double expectedMSE = 1.25;
        double mse = Metric.calculateMSE(data);

        assertEquals(expectedMSE, mse);
    }

    @Test
    void testGetAverageMetricRegression() {
        List<List<Object>> datasetBelow = List.of(
            List.of(1, 2.0),
            List.of(1, 4.0),
            List.of(1, 6.0)
        );
        List<List<Object>> datasetAbove = List.of(
            List.of(1, 8.0),
            List.of(1, 10.0)
        );

        double result = Metric.getAverageMetric(datasetBelow, datasetAbove, 1, "regresion");

        double expected = 2.0;

        assertEquals(expected, result);
    }

    @Test
    void testGetAverageMetricClassification() {
        List<List<Object>> datasetBelow = List.of(
            List.of("A", "M"),
            List.of("A", "M"),
            List.of("A", "F")
        );
        List<List<Object>> datasetAbove = List.of(
            List.of("B", "F"),
            List.of("B", "F")
        );

        double result = Metric.getAverageMetric(datasetBelow, datasetAbove, 1, "clasificacion");

        double expected = 0.5509775004326937;

        assertEquals(expected, result);
    }
}