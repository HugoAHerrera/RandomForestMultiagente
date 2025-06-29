package com.randomforest;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecisionTree {
    private String taskType;
    private int rowsCount;
    private HeaderDto datasetHeader;
    private List<List<Object>> dataset;
    private List<List<Object>> trainingDataset;
    private List<String> trainingColumns;
    private List<List<Object>> testDataset;
    private String targetColumn;
    private int[] trainingDataIndexArray;
    private int targetColumnIndex;
    private int treeMaxDepth = Integer.MAX_VALUE;
    private int minSamplesData = 1;

    public DecisionTree(String taskType, int csvRowsCount, HeaderDto fileHeader, List<List<Object>> dataset, String targetColumn) {
        this.taskType = taskType;
        this.rowsCount = csvRowsCount;
        this.datasetHeader = fileHeader;
        this.dataset = dataset;
        this.targetColumn = targetColumn;
        this.targetColumnIndex = getAttributeIndex(targetColumn);
        List<List<List<Object>>> dataSplit = Bootstrapping.getTrainingElements(dataset, csvRowsCount);
        //this.trainingDataset = dataset; // Poner luego split.get(0);
        this.trainingDataset = dataSplit.get(0);
        this.testDataset = dataSplit.get(1);
        setTrainingColumns();
    }

    public void setTrainingColumns(){
        List<String> candidateAttributes = Bootstrapping.getCandidateAttributes(datasetHeader, targetColumn);

        this.trainingColumns = UtilsFunctions.getClassificationColumns(candidateAttributes);
    }

    public Node generateDecisionTree() {
        if (trainingDataset.size() <= minSamplesData) {
            List<Object> targetColumnSorted = getSortedColumn(trainingDataset, targetColumnIndex);
            return new Node(UtilsFunctions.createLeaf(trainingDataset, targetColumnIndex, taskType));
        }

        Node rootNode = generateTreeRecursive(trainingDataset, 0);
        //return rootNode;
        return pruneTree(rootNode, trainingDataset);
    }

    public Node generateTreeRecursive(List<List<Object>> data, int treeDepth) {

        // Base case
        if (data.size() <= minSamplesData || treeMaxDepth == treeDepth || UtilsFunctions.isDataPure(data,targetColumnIndex)) {
            return new Node(UtilsFunctions.createLeaf(data, targetColumnIndex, taskType));
        }

        List<List<Object>> potencialSplits = getPotencialSplits(data, trainingColumns);

        SplitResult bestSplit = getBestSplit(trainingColumns, potencialSplits, targetColumnIndex, data);

        List<List<Object>> leftData = bestSplit.getBestLeft();
        List<List<Object>> rightData = bestSplit.getBestRight();

        Object leftClass = UtilsFunctions.createLeaf(leftData, targetColumnIndex, taskType);
        Object rightClass = UtilsFunctions.createLeaf(rightData, targetColumnIndex, taskType);

        if (leftData.isEmpty() || rightData.isEmpty()) {
            return new Node(UtilsFunctions.createLeaf(data, targetColumnIndex, taskType));
        }

        if (leftClass != null && leftClass.equals(rightClass)) {
            return new Node(leftClass);
        }

        if (leftClass == null || (leftClass instanceof Double && Double.isNaN((Double) leftClass))
                || rightClass == null || (rightClass instanceof Double && Double.isNaN((Double) rightClass))) {
            return new Node(UtilsFunctions.createLeaf(data, targetColumnIndex, taskType));
        }


        Node rootNode;
        if (datasetHeader.getType(bestSplit.getBestAttribute()).equalsIgnoreCase("Categórica")) {
            rootNode = new Node(bestSplit.getBestAttribute(), "==", bestSplit.getBestSplitValue());
        } else {
            rootNode = new Node(bestSplit.getBestAttribute(), "<=", bestSplit.getBestSplitValue());
        }

        rootNode.setLeftNode(generateTreeRecursive(leftData, treeDepth + 1));
        rootNode.setRightNode(generateTreeRecursive(rightData, treeDepth + 1));
        return rootNode;

    }

    private int getAttributeIndex(String attribute) {
        int index = 0;
        for (String column : datasetHeader.getTypes().keySet()) {
            if (column.equals(attribute)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public List<Object> getColumn(List<List<Object>> data, int columnIndex) {
        List<Object> values = new ArrayList<>();

        for (List<Object> row : data) {
            if (row != null && columnIndex < row.size()) {
                values.add(row.get(columnIndex));
            }
        }

        return values;
    }

    public List<Object> getSortedColumn(List<List<Object>> data, int columnIndex) {
        List<Object> values = getColumn(data, columnIndex);

        boolean allStrings = values.stream().allMatch(val -> val instanceof String);
        if (allStrings) {
            values.sort(Comparator.comparing(Object::toString));
        } else {
            values.sort((first, second) -> {
                double a = ((Number) first).doubleValue();
                double b = ((Number) second).doubleValue();
                return Double.compare(a, b);
            });
        }

        return values;
    }


    public List<List<Object>> getPotencialSplits(List<List<Object>> dataset, List<String> trainingColumns) {
        List<List<Object>> potencialSplits = new ArrayList<>();

        for (String columnName : trainingColumns) {
            int columnIndex = getAttributeIndex(columnName);
            if(datasetHeader.getType(columnName).equalsIgnoreCase("Continua")) {
                List<Object> sortedColumn = getSortedColumn(dataset, columnIndex);

                List<Object> columnSplits = new ArrayList<>();
                for (int i = 1; i < sortedColumn.size(); i++) {
                    Object prev = sortedColumn.get(i - 1);
                    Object current = sortedColumn.get(i);

                    if (prev instanceof Number && current instanceof Number) {
                        double prevVal = ((Number) prev).doubleValue();
                        double currVal = ((Number) current).doubleValue();

                        if (currVal != prevVal) {
                            double midpoint = (prevVal + currVal) / 2.0;
                            columnSplits.add(midpoint);
                        }
                    }
                }

                potencialSplits.add(columnSplits);
            } else{
                List<Object> column = getColumn(dataset, columnIndex);
                Map<Object, Integer> frequencies = UtilsFunctions.getClassFrequencies(column);
                List<Object> uniqueValues = new ArrayList<>(frequencies.keySet());
                potencialSplits.add(uniqueValues);
            }
        }

        return potencialSplits;
    }

    public SplitResult getBestSplit(List<String> trainingColumns, List<List<Object>> potencialSplits, int targetColumnIndex, List<List<Object>> data) {
        double lowesMetricValue = Double.MAX_VALUE;
        String bestAttribute = null;
        Object bestSplitValue = Double.NaN;
        List<List<Object>> bestLeft = new ArrayList<>();
        List<List<Object>> bestRight = new ArrayList<>();

        for (int i = 0; i < trainingColumns.size(); i++) {
            String attribute = trainingColumns.get(i);
            int columnIndex = getAttributeIndex(attribute);
            List<Object> splits = potencialSplits.get(i);

            for (Object splitValue : splits) {
                List<List<List<Object>>> splitResult;

                if (splitValue instanceof Double) {
                    splitResult = UtilsFunctions.splitDataset(data, columnIndex, (Double) splitValue);
                }else{
                    splitResult = UtilsFunctions.splitDataset(data, columnIndex, splitValue.toString());
                }

                List<List<Object>> datasetBelow = splitResult.get(0);
                List<List<Object>> datasetAbove = splitResult.get(1);

                double averageMetric = Metric.getAverageMetric(datasetBelow, datasetAbove, targetColumnIndex, taskType);

                if (averageMetric < lowesMetricValue) {
                    lowesMetricValue = averageMetric;
                    bestAttribute = attribute;
                    bestSplitValue = splitValue;
                    bestLeft = datasetBelow;
                    bestRight = datasetAbove;
                }
            }
        }

        return new SplitResult(bestAttribute, bestSplitValue, bestLeft, bestRight);
    }

    public List<String> predictSamples(List<PredictionRequestDto> samplesToPredict, Node rootNode) {
        List<String> predictions = new ArrayList<>();

        for (PredictionRequestDto sample : samplesToPredict) {
            Map<String, String> features = sample.getFeatures();
            Node currentNode = rootNode;

            while (!currentNode.isLeaf()) {
                String attribute = currentNode.getAttribute();
                String attributeType = datasetHeader.getType(attribute);
                String inputValue = features.get(attribute);

                boolean goLeft;
                if (attributeType.equalsIgnoreCase("Continua")) {
                    try {
                        double value = Double.parseDouble(inputValue);
                        double split = ((Number) currentNode.getValue()).doubleValue();
                        goLeft = value <= split;
                    } catch (NumberFormatException e) {
                        currentNode = null;
                        break;
                    }
                } else {
                    goLeft = inputValue.equals(currentNode.getValue().toString());
                }
                currentNode = goLeft ? currentNode.getLeftNode() : currentNode.getRightNode();
                if (currentNode == null) break;
            }

            if (currentNode != null && currentNode.isLeaf()) {
                predictions.add(currentNode.getPredictedClass().toString());
            } else {
                predictions.add("UNKNOWN");
            }
        }

        return predictions;
    }

    public String getAccuracy(Node rootNode) {
        List<PredictionRequestDto> samples = new ArrayList<>();
        List<Double> actuals = new ArrayList<>();
        List<Double> predictions = new ArrayList<>();

        for (List<Object> row : testDataset) {
            Map<String, String> features = new HashMap<>();
            List<String> columnList = new ArrayList<>(datasetHeader.getTypes().keySet());
            for (int i = 0; i < columnList.size(); i++) {
                String column = columnList.get(i);
                if (!column.equals(targetColumn) && i < row.size()) {
                    features.put(column, row.get(i).toString());
                }
            }

            PredictionRequestDto dto = new PredictionRequestDto();
            dto.setFeatures(features);
            dto.setTarget(targetColumn);
            samples.add(dto);

            Object actualValue = row.get(targetColumnIndex);
            if (taskType.equalsIgnoreCase("regresión") && actualValue instanceof Number) {
                actuals.add(((Number) actualValue).doubleValue());
            }
        }
        List<String> predictionsString = predictSamples(samples, rootNode);

        if (taskType.equalsIgnoreCase("regresión")) {
            for (String pred : predictionsString) {
                try {
                    predictions.add(Double.parseDouble(pred));
                } catch (NumberFormatException e) {
                    predictions.add(0.0);
                }
            }

            double mean = actuals.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            double sumTotalErrors = 0.0;
            double sumErrors = 0.0;

            for (int i = 0; i < actuals.size(); i++) {
                double y = actuals.get(i);
                double yPred = predictions.get(i);
                sumTotalErrors += Math.pow(y - mean, 2);
                sumErrors += Math.pow(y - yPred, 2);
            }

            double rSquared = (sumTotalErrors == 0) ? 1.0 : 1 - (sumErrors / sumTotalErrors);
            return String.format("%.2f%%", rSquared * 100);
        } else {
            int correct = 0;
            for (int i = 0; i < predictionsString.size(); i++) {
                String actual = testDataset.get(i).get(targetColumnIndex).toString();
                String predicted = predictionsString.get(i);
                if (predicted.equals(actual)) {
                    correct++;
                }
            }

            double accuracy = (double) correct / testDataset.size() * 100;
            return String.format("%.2f%%", accuracy);
        }
    }

    public String getAccuracyOnDataset(Node rootNode, List<List<Object>> evaluationDataset) {
        List<PredictionRequestDto> samples = new ArrayList<>();
        List<Double> actuals = new ArrayList<>();
        List<Double> predictions = new ArrayList<>();

        for (List<Object> row : evaluationDataset) {
            Map<String, String> features = new HashMap<>();
            List<String> columnList = new ArrayList<>(datasetHeader.getTypes().keySet());
            for (int i = 0; i < columnList.size(); i++) {
                String column = columnList.get(i);
                if (!column.equals(targetColumn) && i < row.size()) {
                    features.put(column, row.get(i).toString());
                }
            }

            PredictionRequestDto dto = new PredictionRequestDto();
            dto.setFeatures(features);
            dto.setTarget(targetColumn);
            samples.add(dto);

            Object actualValue = row.get(targetColumnIndex);
            if (taskType.equalsIgnoreCase("regresión") && actualValue instanceof Number) {
                actuals.add(((Number) actualValue).doubleValue());
            }
        }

        List<String> predictionsString = predictSamples(samples, rootNode);

        if (taskType.equalsIgnoreCase("regresión")) {
            for (String pred : predictionsString) {
                try {
                    predictions.add(Double.parseDouble(pred));
                } catch (NumberFormatException e) {
                    predictions.add(0.0);
                }
            }

            double mean = actuals.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            double sumTotalErrors = 0.0;
            double sumErrors = 0.0;

            for (int i = 0; i < actuals.size(); i++) {
                double y = actuals.get(i);
                double yPred = predictions.get(i);
                sumTotalErrors += Math.pow(y - mean, 2);
                sumErrors += Math.pow(y - yPred, 2);
            }

            double rSquared = (sumTotalErrors == 0) ? 1.0 : 1 - (sumErrors / sumTotalErrors);
            return String.format("%.2f%%", rSquared * 100);
        } else {
            int correct = 0;
            for (int i = 0; i < predictionsString.size(); i++) {
                String actual = evaluationDataset.get(i).get(targetColumnIndex).toString();
                String predicted = predictionsString.get(i);
                if (predicted.equals(actual)) {
                    correct++;
                }
            }

            double accuracy = (double) correct / evaluationDataset.size() * 100;
            return String.format("%.2f%%", accuracy);
        }
    }

    public Node pruneTree(Node node, List<List<Object>> testDataset) {
        if (node.isLeaf()) {
            return node;
        }

        String attribute = node.getAttribute();
        Object splitVal = node.getValue();
        int attributeIdx = -1;

        int idx = 0;
        for (String col : datasetHeader.getTypes().keySet()) {
            if (col.equals(attribute)) {
                attributeIdx = idx;
                break;
            }
            idx++;
        }

        if (attributeIdx == -1) return node;

        List<List<Object>> leftData = new ArrayList<>();
        List<List<Object>> rightData = new ArrayList<>();

        for (List<Object> row : testDataset) {
            if (attributeIdx >= row.size()) continue;
            Object value = row.get(attributeIdx);
            boolean goLeft;

            if (datasetHeader.getType(attribute).equalsIgnoreCase("continua")) {
                if (value instanceof Number) {
                    double val = ((Number) value).doubleValue();
                    double split = ((Number) splitVal).doubleValue();
                    goLeft = val <= split;
                } else continue;
            } else {
                goLeft = value != null && value.toString().equals(splitVal.toString());
            }

            if (goLeft) leftData.add(row);
            else rightData.add(row);
        }

        node.setLeftNode(pruneTree(node.getLeftNode(), leftData));
        node.setRightNode(pruneTree(node.getRightNode(), rightData));

        Object leafClass = UtilsFunctions.createLeaf(trainingDataset, targetColumnIndex, taskType);
        Node prunedLeaf = new Node(leafClass);

        String accTreeStr = getAccuracyOnDataset(node, testDataset);
        String accLeafStr = getAccuracyOnDataset(prunedLeaf, testDataset);

        double accTree = Double.parseDouble(accTreeStr.replaceAll("[^\\d.\\-]", ""));
        double accLeaf = Double.parseDouble(accLeafStr.replaceAll("[^\\d.\\-]", ""));

        if (accLeaf >= accTree) {
            System.out.println("Pruned");
            return prunedLeaf;
        } else {
            return node;
        }
    }

}

