package com.randomforest.randomforest;

import java.util.*;

import com.randomforest.dto.HeaderDto;

public class Bootstrapping {

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

