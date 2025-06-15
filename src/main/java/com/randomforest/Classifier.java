package com.randomforest;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Classifier {

    public enum Type {
        CONTINUA,
        CATEGORICA
    }

    private final Map<String, Type> columnTypes = new HashMap<>();
    private final Map<String, Set<String>> columnValues = new HashMap<>();
    private static final int numberMaxValues = 15;
    private final Set<String> continuousColumns = new HashSet<>();

    public void columnsInit(String[] header) {
        columnTypes.clear();
        columnValues.clear();
        continuousColumns.clear();
        for (String col : header) {
            columnValues.put(col, new HashSet<>());
        }
    }

    public void analizeRow(String[] row, String[] header) {
        for (int i = 0; i < row.length; i++) {
            String col = header[i];
            if (continuousColumns.contains(col)) continue;
            Set<String> values = columnValues.get(col);
            values.add(row[i]);
            if (values.size() > numberMaxValues) {
                columnTypes.put(col, Type.CONTINUA);
                continuousColumns.add(col);
                columnValues.remove(col);
            }
        }
    }

    public void finishClassification() {
        for (String col : columnValues.keySet()) {
            if (!columnTypes.containsKey(col)) {
                columnTypes.put(col, Type.CATEGORICA);
            }
        }
        columnValues.clear();
    }
}

