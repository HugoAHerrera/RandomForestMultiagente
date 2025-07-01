package com.randomforest.randomforest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UtilsFunctions {
    /**
     * Creates a final leaf node.
     * In regression tasks, it returns the mean value of the given dataset.
     * In classification tasks, it returns the most frequent class.
     *
     * @param data dataset
     * @param columnIndex target column index
     * @param taskType "regresion" or "clasificacion"
     * @return leaf
     */
    public static Object createLeaf(List<List<Object>> data, int columnIndex, String taskType) {
        List<Object> columnValues = new ArrayList<>();

        for (List<Object> row : data) {
            if (columnIndex < row.size()) {
                columnValues.add(row.get(columnIndex));
            }
        }

        if (taskType.equalsIgnoreCase("regresion")) {
            double sum = 0.0;
            int count = 0;

            for (Object val : columnValues) {
                if (val instanceof Number) {
                    sum += ((Number) val).doubleValue();
                    count++;
                }
            }
            if (count == 0) {
                return null;
            }

            return sum / count;
        } else {
            Map<Object, Integer> frequencyMap = getClassFrequencies(columnValues);

            Object leaf = null;
            int maxFrequency = -1;

            for (Map.Entry<Object, Integer> entry : frequencyMap.entrySet()) {
                if (entry.getValue() > maxFrequency) {
                    maxFrequency = entry.getValue();
                    leaf = entry.getKey();
                }
            }

            return leaf;
        }
    }

    /**
     * Returns column values frecuencies.
     *
     * @param data dataset
     * @return A map where keys are unique values from the column and values are their frequencies
     * For example: {A=3, B=2, C=1} 
     */
    public static Map<Object, Integer> getClassFrequencies(List<Object> data) {
        Map<Object, Integer> frequencyValues = new HashMap<>();

        for (Object value : data) {
            if (value != null) {
                frequencyValues.put(value, frequencyValues.getOrDefault(value, 0) + 1);
            }
        }

        return frequencyValues;
    }

    /**
     * Returns whether a dataset is pure based on a specific column.
     *
     * @param data The dataset to evaluate.
     * @param columnIndex The index of the column to check for purity
     * @return True if there is only one unique value in the column. False if there are multiple
     */
    public static boolean isDataPure(List<List<Object>> data, int columnIndex) {
        Set<Object> types = new HashSet<>();

        for (List<Object> row : data) {
            if (columnIndex < row.size()) {
                Object value = row.get(columnIndex);
                if (value != null) {
                    types.add(value);
                    if (types.size() > 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the columns to be used in decision tree classification.
     *
     * @param columnNames List of all potential columns for building the decision tree
     * @return List of selected columns
     */
    public static List<String> getClassificationColumns(List<String> columnNames) {
        List<String> selectedColumns = new ArrayList<>(columnNames);
        int numberColumns = (int) Math.round(Math.sqrt(columnNames.size()));

        Collections.shuffle(selectedColumns);
        return selectedColumns.subList(0, Math.min(numberColumns, selectedColumns.size()));
    }

    /**
     * Splits the data into two datasets based on a given criterion.
     *
     * @param data dataset before the split
     * @param columnIndex Column index used for splitting
     * @param splitValue Value of the column to split the data. Can be a number of a string
     * @return Two datasets, one surpass the split value >= for numbers and == for string.
     */
    public static List<List<List<Object>>> splitDataset(List<List<Object>> data, int columnIndex, Object splitValue) {
        List<List<Object>> dataBelow = new ArrayList<>();
        List<List<Object>> dataAbove = new ArrayList<>();

        for (List<Object> row : data) {
            if (columnIndex < row.size()) {
                Object value = row.get(columnIndex);
                if (value instanceof Number) {
                    double valueDouble = ((Number) value).doubleValue();
                    double convertedValue = ((Number) splitValue).doubleValue();
                    if (valueDouble <= convertedValue) {
                        dataBelow.add(row);
                    } else {
                        dataAbove.add(row);
                    }
                } else {
                    if (value != null && value.toString().equals(splitValue.toString())) {
                        dataBelow.add(row);
                    } else {
                        dataAbove.add(row);
                    }
                }
            }
        }

        List<List<List<Object>>> result = new ArrayList<>();
        result.add(dataBelow);
        result.add(dataAbove);
        return result;
    }
}
