package com.randomforest.service;

import com.randomforest.model.Prediction;
import com.randomforest.model.PredictionType;
import com.randomforest.repository.PredictionRepository;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.randomforest.dto.PredictionResultDto;
import com.randomforest.service.UserService;
import com.randomforest.model.User;
import com.randomforest.dto.HeaderDto;
import com.randomforest.dto.ChunkDto;
import com.randomforest.dto.PredictionRequestDto;
import com.randomforest.randomforest.DecisionTree;
import com.randomforest.randomforest.Node;
import com.randomforest.randomforest.SampleResults;
import org.springframework.scheduling.annotation.Async;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVFormat;

import java.io.ByteArrayOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private HeaderDto header;
    private CSVPrinter csvPrinter;
    private ByteArrayOutputStream csvBuffer;
    private int csvRowCount = 0;
    private int amountTrees;

    private List<List<Object>> dataset = new ArrayList<>();

    public PredictionService(PredictionRepository predictionRepository, UserService userService) {
        this.predictionRepository = predictionRepository;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    public List<Prediction> getPredictionsByUserId(Integer userId) {
        return predictionRepository.findByUserId(userId);
    }

    public void storeResults(List<PredictionResultDto> resultados) {
        for (PredictionResultDto dto : resultados) {
            Integer userId = userService.findByUsername(dto.getUserName())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserName()));

            Prediction prediction = new Prediction();
            prediction.setUserId(userId);

            PredictionType tipo = "regresion".equalsIgnoreCase(dto.getTask())
                ? PredictionType.Regresión
                : PredictionType.Clasificación;
            prediction.setType(tipo);

            try {
                prediction.setResult(objectMapper.writeValueAsString(dto.getTarget()));
                prediction.setParameters(objectMapper.writeValueAsString(dto.getFeatures()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error creating JSON", e);
            }

            prediction.setFileName(dto.getFileName());

            double roundedAccuracy = Math.round(dto.getAccuracy() * 100.0) / 100.0;
            prediction.setAccuracy(roundedAccuracy + "%");

            predictionRepository.save(prediction);
        }
    }

    public void sendHeader(HeaderDto header) throws Exception {
        setHeader(header);

        csvBuffer = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(csvBuffer));
        String[] columns = header.getTypes().keySet().toArray(new String[0]);
        csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(columns));
    }

    public void sendChunk(ChunkDto chunk) throws Exception {
        for (Map<String, String> row : chunk.getRows()) {
            List<Object> parsedRow = new ArrayList<>();
            boolean skipRow = false;

            for (String column : header.getTypes().keySet()) {
                String type = header.getTypes().get(column);
                Object rawValue = row.get(column);

                if (rawValue == null) {
                    skipRow = true;
                    break;
                }

                if ("Continua".equalsIgnoreCase(type)) {
                    try {
                        float val = Float.parseFloat(rawValue.toString());
                        if (val == (int) val) {
                            parsedRow.add((int) val);
                        } else {
                            parsedRow.add(val);
                        }
                    } catch (NumberFormatException e) {
                        skipRow = true;
                        break;
                    }
                } else {
                    parsedRow.add(rawValue.toString());
                }
            }

            if (!skipRow) {
                dataset.add(parsedRow);
                csvPrinter.printRecord(parsedRow);
                csvPrinter.flush();
                incrementCsvRowCount();
            }
        }
    }

    @Async
    public void sendRequestedPredictions(List<PredictionRequestDto> predictionsRequestedList) throws Exception {
        List<SampleResults> resultsList = new ArrayList<>();
        String taskType = null;
        String targetColumn = null;

        for (PredictionRequestDto dto : predictionsRequestedList) {
            targetColumn = dto.getTarget();
            taskType = dto.getTask();
            break;
        }

        SampleResults result = new SampleResults();
        Map<String, Object> summary = result.getSampleOutputs();
        List<Double> accuracies = new ArrayList<>();

        for (PredictionRequestDto sampleData : predictionsRequestedList) {
            if ("clasificacion".equalsIgnoreCase(taskType)) {
                summary.put(sampleData.getName(), new HashMap<String, Integer>());
            } else {
                summary.put(sampleData.getName(), new ArrayList<Double>());
            }
        }

        for (int i = 0; i < 100; i++) {
            DecisionTree decisionTree = new DecisionTree(taskType, getCsvRowCount(), getHeader(), dataset, targetColumn);
            Node node = decisionTree.generateDecisionTree();
            String accuracyStr = decisionTree.getAccuracy(node);
            String cleanedAcc = accuracyStr.replace(",", ".").replaceAll("[^\\d.\\-]", "");
            double accValue = Double.parseDouble(cleanedAcc);
            accuracies.add(accValue);

            List<String> predictions = decisionTree.predictSamples(predictionsRequestedList, node);

            for (int j = 0; j < predictionsRequestedList.size(); j++) {
                String name = predictionsRequestedList.get(j).getName();
                String pred = predictions.get(j);

                if ("clasificacion".equalsIgnoreCase(taskType)) {
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

        if ("regresion".equalsIgnoreCase(taskType)) {
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

        resultsList.add(result);

        List<PredictionResultDto> predictionResults = new ArrayList<>();
        Map<String, List<Object>> accumulatedSamples = new HashMap<>();
        Map<String, List<Double>> accuracyMap = new HashMap<>();

        for (SampleResults sampleResult : resultsList) {
            Map<String, Object> sampleOutputs = sampleResult.getSampleOutputs();

            for (Map.Entry<String, Object> entry : sampleOutputs.entrySet()) {
                String sampleName = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, ?> content = (Map<String, ?>) value;

                    accumulatedSamples.putIfAbsent(sampleName, new ArrayList<>());
                    accumulatedSamples.get(sampleName).add(content);

                    accuracyMap.putIfAbsent(sampleName, new ArrayList<>());
                    accuracyMap.get(sampleName).add(sampleResult.getAverageAccuracy());
                }
            }
        }

        for (Map.Entry<String, List<Object>> entry : accumulatedSamples.entrySet()) {
            String sampleName = entry.getKey();
            List<Object> values = entry.getValue();

            PredictionRequestDto predictionRequest = predictionsRequestedList
                .stream()
                .filter(dto -> dto.getName().equals(sampleName))
                .findFirst()
                .orElse(null);

            if (predictionRequest == null) continue;

            List<Double> accuraciesSample = accuracyMap.get(sampleName);
            double averageAccuracy = accuraciesSample.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            Map<String, String> targetMap;

            if ("clasificacion".equalsIgnoreCase(predictionRequest.getTask())) {
                Map<String, Integer> counts = new HashMap<>();

                for (Object classificationObj : values) {
                    if (classificationObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Integer> classes = (Map<String, Integer>) classificationObj;
                        for (Map.Entry<String, Integer> classEntry : classes.entrySet()) {
                            counts.merge(classEntry.getKey(), classEntry.getValue(), Integer::sum);
                        }
                    }
                }

                String predictedClass = Collections.max(counts.entrySet(), Map.Entry.comparingByValue()).getKey();
                targetMap = Map.of(predictionRequest.getTarget(), predictedClass);

            } else if ("regresion".equalsIgnoreCase(predictionRequest.getTask())) {
                double sumValues = 0.0;
                int valueCount = 0;

                for (Object regressionObj : values) {
                    if (regressionObj instanceof Map) {
                        Object rawValue = ((Map<?, ?>) regressionObj).get("value");
                        if (rawValue instanceof Number) {
                            sumValues += ((Number) rawValue).doubleValue();
                            valueCount++;
                        }
                    }
                }

                double averageValue = (valueCount > 0) ? Math.round((sumValues / valueCount) * 100.0) / 100.0 : 0.0;
                targetMap = Map.of(predictionRequest.getTarget(), String.valueOf(averageValue));

            } else {
                continue;
            }

            PredictionResultDto predictionResult = new PredictionResultDto(
                predictionRequest.getUserName(),
                targetMap,
                predictionRequest.getTask(),
                predictionRequest.getFeatures(),
                predictionRequest.getFileName(),
                averageAccuracy
            );
            System.out.println("Predicciones hechas " + LocalDateTime.now());
            predictionResults.add(predictionResult);
        }
        this.storeResults(predictionResults);
    }

    public void incrementCsvRowCount() {
        this.csvRowCount++;
    }

}
