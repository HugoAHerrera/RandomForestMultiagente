package com.randomforest;

import java.util.*;

public class UtilsFunctions {
    public static Object createLeaf(List<List<Object>> data, int columnIndex, String taskType) {
        List<Object> columnValues = new ArrayList<>();

        for (List<Object> row : data) {
            if (columnIndex < row.size()) {
                columnValues.add(row.get(columnIndex));
            }
        }

        if (taskType.equalsIgnoreCase("regresión")) {
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


    public static Map<Object, Integer> getClassFrequencies(List<Object> data) {
        Map<Object, Integer> frequencyValues = new HashMap<>();

        for (Object value : data) {
            if (value != null) {
                frequencyValues.put(value, frequencyValues.getOrDefault(value, 0) + 1);
            }
        }

        return frequencyValues;
    }

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

    public static List<String> getClassificationColumns(List<String> columnNames) {
        List<String> selectedColumns = new ArrayList<>(columnNames);
        int numberColumns = (int) Math.round(Math.sqrt(columnNames.size()));

        Collections.shuffle(selectedColumns);
        return selectedColumns.subList(0, Math.min(numberColumns, selectedColumns.size()));
    }

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


    public static List<Object> getUniqueValues(List<Object> data) {
        Set<Object> uniqueSet = new LinkedHashSet<>(data);
        return new ArrayList<>(uniqueSet);
    }
}
