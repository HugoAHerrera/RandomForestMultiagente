package com.randomforest.randomforest;

import java.util.*;

import com.randomforest.dto.HeaderDto;

public class Bootstrapping {
    /**
     * Splits a dataset into a training dataset and a testing dataset
     *
     * @param dataset the original dataset
     * @param sampleSize the number of rows the training dataset will have
     * @return a list of two lists: the first is the training dataset, the second is the testing dataset
     */
    public static List<List<List<Object>>> getTrainingElements(List<List<Object>> dataset, int sampleSize) {
        Random random = new Random();
        List<List<Object>> training = new ArrayList<>();
        List<Integer> selectedIndices = new ArrayList<>();
        Set<Integer> selectedSet = new HashSet<>();

        for (int i = 0; i < sampleSize; i++) {
            int index = random.nextInt(dataset.size());
            training.add(dataset.get(index));
            selectedIndices.add(index);
            selectedSet.add(index);
        }

        List<List<Object>> test = new ArrayList<>();
        for (int i = 0; i < dataset.size(); i++) {
            if (!selectedSet.contains(i)) {
                test.add(dataset.get(i));
            }
        }

        List<List<List<Object>>> result = new ArrayList<>();
        result.add(training);
        result.add(test);
        return result;
    }

    /**
     * Sets the columns that will be used for training the decision tree
     *
     * @param header the dataset header containing column metadata. {"ColumnName": "Categorica" or "Continua"}
     * @param targetColumn the name of the target column to exclude
     * @return a list of attribute names excluding the target column
     */
    public static List<String> getCandidateAttributes(HeaderDto header, String targetColumn) {
        List<String> attributes = new ArrayList<>();
        for (String col : header.getTypes().keySet()) {
            if (!col.equals(targetColumn)) {
                attributes.add(col);
            }
        }
        return attributes;
    }
}

