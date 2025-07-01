package com.randomforest.randomforest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class UtilsFunctionsTest {
    @Test
    void testCreateLeafClassification() {
        List<List<Object>> data = new ArrayList<>();
        data.add(List.of(1, 25, "M"));
        data.add(List.of(1, 28, "F"));
        data.add(List.of(2, 30, "M"));

        Object leaf = UtilsFunctions.createLeaf(data, 2, "clasificacion");

        assertEquals("M", leaf);
    }

    @Test
    void testCreateLeafRegression() {
        List<List<Object>> data = new ArrayList<>();
        data.add(List.of(1, 25, "M"));
        data.add(List.of(1, 35, "F"));
        data.add(List.of(2, 30, "M"));

        Object leaf = UtilsFunctions.createLeaf(data, 1, "regresion");

        assertEquals(30.0, leaf);
    }

    @Test
    void testCreateLeafRegressionDecimals() {
        List<List<Object>> data = new ArrayList<>();
        data.add(List.of(1, 25.5, "M"));
        data.add(List.of(1, 33.25, "F"));
        data.add(List.of(2, 28, "M"));

        Object leaf = UtilsFunctions.createLeaf(data, 1, "regresion");

        assertEquals(28.916666666666668, (double) leaf, 0.0001); //+-0.0001 value tolerance
    }

    @Test
    void testGetClassFrequencies() {
        List<Object> data = List.of("M", "F", "M", "F", "M");
        Map<Object, Integer> frequencies = UtilsFunctions.getClassFrequencies(data);

        assertEquals(2, frequencies.get("F"));
        assertEquals(3, frequencies.get("M"));
        assertEquals(2, frequencies.size());
    }

    @Test
    void testGetClassMultipleFrequencies() {
        List<Object> data = List.of("Iris-setosa", "Iris-virginica", "Iris-versicolor", "Iris-versicolor", "Iris-virginica", "Iris-setosa");
        Map<Object, Integer> frequencies = UtilsFunctions.getClassFrequencies(data);

        assertEquals(2, frequencies.get("Iris-setosa"));
        assertEquals(2, frequencies.get("Iris-virginica"));
        assertEquals(2, frequencies.get("Iris-versicolor"));
        assertEquals(3, frequencies.size());
    }

    @Test
    void testIsDataPureTrue() {
        List<List<Object>> data = new ArrayList<>();
        data.add(List.of(1, 25.5, "M"));
        data.add(List.of(1, 33.25, "M"));
        data.add(List.of(2, 28, "M"));

        assertTrue(UtilsFunctions.isDataPure(data, 2));
    }

    @Test
    void testIsDataPureFalse() {
        List<List<Object>> data = new ArrayList<>();
        data.add(List.of(1, 25.5, "M"));
        data.add(List.of(1, 33.25, "F"));
        data.add(List.of(2, 28, "M"));

        assertFalse(UtilsFunctions.isDataPure(data, 2));
    }

    @Test
    void testGetClassificationColumnsNumber() {
        List<String> columns = List.of("A", "B", "C", "D");
        List<String> result = UtilsFunctions.getClassificationColumns(columns);

        assertEquals(2, result.size());
    }

    @Test
    void testGetClassificationColumnsShuffle() {
        List<String> columns = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
        List<String> firstResult = UtilsFunctions.getClassificationColumns(columns);

        // Verify that shuffle returns different columns
        boolean foundDifferent = false;
        for (int i = 1; i < 100; i++) {
            List<String> currentResult = UtilsFunctions.getClassificationColumns(columns);
            if (!currentResult.equals(firstResult)) {
                foundDifferent = true;
                break;
            }
        }

        assertTrue(foundDifferent);
    }

    @Test
    void testSplitDatasetContinuousColumn() {
        List<List<Object>> data = new ArrayList<>();
        data.add(List.of(1, 20.5, "M"));
        data.add(List.of(1, 33.25, "M"));
        data.add(List.of(2, 28, "F"));
        data.add(List.of(1, 23, "F"));
        data.add(List.of(1, 27, "M"));
        data.add(List.of(2, 40, "F"));

        List<List<List<Object>>> result = UtilsFunctions.splitDataset(data, 1, 27.5);

        List<List<Object>> belowData = result.get(0);
        List<List<Object>> aboveData = result.get(1);

        for (List<Object> row : belowData) {
            double value = ((Number) row.get(1)).doubleValue();
            assertTrue(value <= 27.5);
        }

        for (List<Object> row : aboveData) {
            double value = ((Number) row.get(1)).doubleValue();
            assertTrue(value > 27.5);
        }

        assertEquals(3, result.get(0).size());
        assertEquals(3, result.get(1).size());
    }

    @Test
    void testSplitDatasetCategoricalColumn() {
        List<List<Object>> data = new ArrayList<>();
        data.add(List.of(1, 20.5, "M"));
        data.add(List.of(1, 33.25, "M"));
        data.add(List.of(2, 28, "F"));
        data.add(List.of(1, 23, "F"));
        data.add(List.of(1, 27, "M"));
        data.add(List.of(2, 40, "F"));

        List<List<List<Object>>> result = UtilsFunctions.splitDataset(data, 2, "M");

        List<List<Object>> belowData = result.get(0);
        List<List<Object>> aboveData = result.get(1);

        for (List<Object> row : belowData) {
            String value = (String) row.get(2);
            assertTrue(value.equals("M"));
        }

        for (List<Object> row : aboveData) {
            String value = (String) row.get(2);
            assertTrue(value.equals("F"));
        }

        assertEquals(3, result.get(0).size());
        assertEquals(3, result.get(1).size());
    }
    
}