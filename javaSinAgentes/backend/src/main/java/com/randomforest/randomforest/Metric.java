package com.randomforest.randomforest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.randomforest.randomforest.UtilsFunctions;

public class Metric {
    /**
     * Calculates the entropy of a dataset.
     *
     * @param data List of the target column values of the dataset
     * @return The entropy value as a double
     */
    public static double calculateEntropy(List<Object> data) {
        Map<Object, Integer> classification = UtilsFunctions.getClassFrequencies(data);

        int total = data.size();
        double entropy = 0.0;

        for (Map.Entry<Object, Integer> entry : classification.entrySet()) {
            double probability = (double) entry.getValue() / total;
            entropy += -probability * (Math.log(probability) / Math.log(2));
        }

        return entropy;
    }

    /**
     * Calculates the MSE.
     *
     * @param data List of the target column values of the dataset
     * @return The MSE value as a double
     */
    public static double calculateMSE(List<Object> data) {
        double sum = 0.0;
        double mean;

        List<Double> values = new ArrayList<>();
        for (Object val : data) {
            if (val instanceof Number) {
                values.add(((Number) val).doubleValue());
            }
        }

        mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);

        for (double value : values) {
            double error = value - mean;
            sum += error * error;
        }

        return sum / values.size();
    }

    /**
     * Calculates the average metric value of an already splitted dataset.
     *
     * @param datasetBelow dataset with values that meet the split criteria
     * @param datasetAbove dataset with values that did not meet the split criteria
     * @param targetColumnIndex target column index
     * @param function String that specifies if is a regression task or classification task
     * @return The average metric value of the splitted dataset
     */
    public static double getAverageMetric(List<List<Object>> datasetBelow, List<List<Object>> datasetAbove, int targetColumnIndex, String function) {
        int totalSamples = datasetBelow.size() + datasetAbove.size();
        double probabilityBelow = (double) datasetBelow.size() / totalSamples;
        double probabilityAbove = (double) datasetAbove.size() / totalSamples;

        List<Object> belowValues = new ArrayList<>();
        for (List<Object> row : datasetBelow) {
            if (targetColumnIndex < row.size()) {
                belowValues.add(row.get(targetColumnIndex));
            }
        }

        List<Object> aboveValues = new ArrayList<>();
        for (List<Object> row : datasetAbove) {
            if (targetColumnIndex < row.size()) {
                aboveValues.add(row.get(targetColumnIndex));
            }
        }
        double metricBelow;
        double metricAbove;
        if (function.equalsIgnoreCase("regresion")) {
            metricBelow = calculateMSE(belowValues);
            metricAbove = calculateMSE(aboveValues);
        }else{
            metricBelow = calculateEntropy(belowValues);
            metricAbove = calculateEntropy(aboveValues);
        }


        return probabilityBelow * metricBelow + probabilityAbove * metricAbove;
    }

}