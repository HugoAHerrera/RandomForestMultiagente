package com.randomforest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class Main {
    private HeaderDto header;
    private CSVPrinter csvPrinter;
    private ByteArrayOutputStream csvBuffer;
    private int rowsCount;
    private int amountTrees = 5;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        try {
            header = new HeaderDto();
            Map<String, String> types = new LinkedHashMap<>();
            types.put("type", "Categórica");
            types.put("fixed acidity", "Continua");
            types.put("volatile acidity", "Continua");
            types.put("citric acid", "Continua");
            types.put("residual sugar", "Continua");
            types.put("chlorides", "Continua");
            types.put("free sulfur dioxide", "Continua");
            types.put("total sulfur dioxide", "Continua");
            types.put("density", "Continua");
            types.put("pH", "Continua");
            types.put("sulphates", "Continua");
            types.put("alcohol", "Continua");
            types.put("quality", "Continua");
            header.setTypes(types);

            File csvFile = new File("src_antiguo/data/winequality-combined.csv");
            FileReader reader = new FileReader(csvFile);

            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withHeader(types.keySet().toArray(new String[0]))
                    .withSkipHeaderRecord());

            List<List<Object>> dataset = new ArrayList<>();
            rowsCount = 0;

            for (CSVRecord record : parser) {
                List<Object> parsedRow = new ArrayList<>();
                for (String column : types.keySet()) {
                    String type = types.get(column);
                    String rawValue = record.get(column);
                    if ("Continua".equalsIgnoreCase(type)) {
                        try {
                            float val = Float.parseFloat(rawValue);
                            if (val == (int) val) {
                                parsedRow.add((int) val);
                            } else {
                                parsedRow.add(val);
                            }
                        } catch (NumberFormatException e) {
                            parsedRow.add(rawValue);
                        }
                    } else {
                        parsedRow.add(rawValue);
                    }
                }
                dataset.add(parsedRow);
                rowsCount++;
            }
            parser.close();
            reader.close();

            String targetColumn = "type";

            Map<String, String> features = new HashMap<>();
            features.put("fixed acidity", "7.0");
            features.put("volatile acidity", "0.27");
            features.put("citric acid", "0.36");
            features.put("residual sugar", "20.7");
            features.put("chlorides", "0.045");
            features.put("free sulfur dioxide", "45.0");
            features.put("total sulfur dioxide", "170.0");
            features.put("density", "1.001");
            features.put("pH", "3.0");
            features.put("sulphates", "0.45");
            features.put("quality", "6.0");

            String taskType = "Clasificación";

            PredictionRequestDto sample = new PredictionRequestDto();
            sample.setName("Muestra1");
            sample.setTarget("type");
            sample.setTask(taskType);
            sample.setFeatures(features);

            PredictionRequestDto sample2 = new PredictionRequestDto();
            sample2.setName("Muestra2");
            sample2.setTarget("type");
            sample2.setTask(taskType);
            sample2.setFeatures(features);

            List<PredictionRequestDto> samples = Arrays.asList(sample, sample2);

            SampleResults result = new SampleResults();
            Map<String, Object> summary = result.getSampleOutputs();
            List<Double> accuracies = new ArrayList<>();

            for (PredictionRequestDto sampleData : samples) {
                if (taskType.equalsIgnoreCase("clasificación")) {
                    summary.put(sampleData.getName(), new HashMap<String, Integer>());
                } else {
                    summary.put(sampleData.getName(), new ArrayList<Double>());
                }
            }

            for (int i = 0; i < amountTrees; i++) {
                DecisionTree tree = new DecisionTree(taskType, rowsCount, header, dataset, targetColumn);
                Node node = tree.generateDecisionTree();

                String accuracyStr = tree.getAccuracy(node);
                String clean = accuracyStr.replace(",", ".").replaceAll("[^\\d.\\-]", "");
                double val = Double.parseDouble(clean);
                accuracies.add(val);

                List<String> predictions = tree.predictSamples(samples, node);

                for (int j = 0; j < samples.size(); j++) {
                    String name = samples.get(j).getName();
                    String pred = predictions.get(j);

                    if (taskType.equalsIgnoreCase("clasificación")) {
                        Map<String, Integer> count = (Map<String, Integer>) summary.get(name);
                        count.merge(pred, 1, Integer::sum);
                    } else {
                        List<Double> values = (List<Double>) summary.get(name);
                        try {
                            values.add(Double.parseDouble(pred));
                        } catch (NumberFormatException e) {
                            values.add(0.0);
                        }
                    }
                }
            }

            if (taskType.equalsIgnoreCase("regresión")) {
                for (String key : new ArrayList<>(summary.keySet())) {
                    List<Double> values = (List<Double>) summary.get(key);
                    double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    Map<String, Double> wrapped = new HashMap<>();
                    wrapped.put("value", mean);

                    summary.put(key, wrapped);
                }
            }

            double meanAccuracy = accuracies.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            result.setAverageAccuracy(meanAccuracy);

            System.out.println(result);


/*
            Map<String, String> features = new HashMap<>();
            features.put("season", "1");
            features.put("yr", "0");
            features.put("mnth", "1");
            features.put("holiday", "0");
            features.put("weekday", "6");
            features.put("workingday", "0");
            features.put("weathersit", "2");
            features.put("temp", "0.344167");
            features.put("atemp", "0.363625");
            features.put("hum", "0.805833");
            features.put("windspeed", "0.160446");
            features.put("day_of_year", "1");
            features.put("day_of_month", "1");
            features.put("quarter", "1");
            features.put("week", "52");
            features.put("is_month_end", "False");
            features.put("is_month_start", "True");
            features.put("is_quarter_end", "False");
            features.put("is_quarter_start", "True");
            features.put("is_year_end", "False");
            features.put("is_year_start", "True");

            PredictionRequestDto sample = new PredictionRequestDto();
            sample.setName("Muestra1");
            sample.setTarget("label");
            sample.setTask("regresión");
            sample.setFeatures(features);

            List<PredictionRequestDto> samples = Arrays.asList(sample, sample);

            List<String> prediction = tree.predictSamples(samples, myNode);
            System.out.println("Predicción: " + prediction);
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
