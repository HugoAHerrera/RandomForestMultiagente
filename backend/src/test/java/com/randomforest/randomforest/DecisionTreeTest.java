package com.randomforest.randomforest;

import com.randomforest.dto.HeaderDto;
import com.randomforest.randomforest.Node;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.List;
import java.util.Map;

public class DecisionTreeTest {
    private DecisionTree tree;

    private String taskType;
    private int csvRowsCount;
    private HeaderDto headerDto;
    private List<List<Object>> data;
    private String targetColumn;

    @BeforeEach
    void setUp() {
        data = new ArrayList<>();
        data.add(List.of(1, "M", 20.5));
        data.add(List.of(1, "M", 33.25));
        data.add(List.of(2, "F", 28));
        data.add(List.of(1, "F", 23));
        data.add(List.of(1, "M", 27));
        data.add(List.of(2, "F", 40));

        headerDto = new HeaderDto();
        Map<String, String> columnTypes = new HashMap<>();
        columnTypes.put("workingYears", "Continua");
        columnTypes.put("salary", "Continua");
        columnTypes.put("genre", "Categórica");
        headerDto.setTypes(columnTypes); // Columns are sorted alphabetically

        taskType = "clasificacion";
        csvRowsCount = 6;
        targetColumn = "genre";

        tree = new DecisionTree(taskType, csvRowsCount, headerDto, data, targetColumn);
    }

    @Test
    void testConstructorSetUp(){
        assertEquals(taskType, tree.getTaskType());
        assertEquals(csvRowsCount, tree.getRowsCount());
        assertEquals(headerDto, tree.getDatasetHeader());
        assertEquals(data, tree.getDataset());
        assertEquals(targetColumn, tree.getTargetColumn());
        assertEquals(1, tree.getTargetColumnIndex());
        assertNotNull(tree.getTrainingDataset());
        assertNotNull(tree.getTestDataset());
        assertEquals(tree.getTrainingDataset().size(), csvRowsCount);
    }

    @Test
    void testGetAttributeIndex() throws Exception {
        var method = DecisionTree.class.getDeclaredMethod("getAttributeIndex", String.class);
        method.setAccessible(true);
        int index = (int) method.invoke(tree, "salary");
        assertEquals(2, index);
    }

    @Test
    void testGetColumnString() throws Exception {
        var method = DecisionTree.class.getDeclaredMethod("getColumn", List.class, int.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Object> column = (List<Object>) method.invoke(tree, data, 1);

        List<Object> expected = List.of("M", "M", "F", "F", "M", "F");
        assertEquals(expected, column);
    }

    @Test
    void testGetColumnNumbers() throws Exception {
        var method = DecisionTree.class.getDeclaredMethod("getColumn", List.class, int.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Object> column = (List<Object>) method.invoke(tree, data, 2);

        List<Object> expected = List.of(20.5, 33.25, 28, 23, 27, 40);
        assertEquals(expected, column);
    }

    @Test
    void testSetTrainingColumns() throws Exception {
        var method = DecisionTree.class.getDeclaredMethod("setTrainingColumns");
        method.setAccessible(true);

        Set<List<String>> sets = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            method.invoke(tree);

            var trainingColumns = tree.getTrainingColumns();

            assertNotNull(trainingColumns);

            sets.add(trainingColumns);
        }

        // At least there are 2 different training columns
        assertTrue(sets.size() >= 2);
    }

    @Test
    void testGetSortedColumn() throws Exception {
        var method = DecisionTree.class.getDeclaredMethod("getSortedColumn", List.class, int.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Object> sortedNumbers = (List<Object>) method.invoke(tree, data, 2);
        List<Object> expectedNumbers = List.of(20.5, 23, 27, 28, 33.25, 40);
        assertEquals(expectedNumbers, sortedNumbers);

        @SuppressWarnings("unchecked")
        List<Object> sortedStrings = (List<Object>) method.invoke(tree, data, 1);
        List<String> expectedStrings = List.of("F", "F", "F", "M", "M", "M");
        assertEquals(expectedStrings, sortedStrings);
    }

    @Test
    void testGetPotencialSplits() throws Exception {
        var method = DecisionTree.class.getDeclaredMethod("getPotencialSplits", List.class, List.class);
        method.setAccessible(true);

        List<String> trainingColumns = List.of("workingYears", "salary", "genre");

        @SuppressWarnings("unchecked")
        List<List<Object>> potencialSplits = (List<List<Object>>) method.invoke(tree, data, trainingColumns);

        assertEquals(3, potencialSplits.size());

        List<Object> expectedWorkingYearsSplits = List.of(1.5);
        List<Object> expectedSalarySplits = List.of(21.75, 25.0, 27.5, 30.625, 36.625);
        List<Object> expectedGenreSplits = List.of("F", "M");

        assertEquals(expectedWorkingYearsSplits, potencialSplits.get(0));
        assertEquals(expectedSalarySplits, potencialSplits.get(1));
        assertEquals(expectedGenreSplits, potencialSplits.get(2));
    }

    @Test
    void testGetBestSplitCategoricalColumn() throws Exception {
        var method = DecisionTree.class.getDeclaredMethod("getBestSplit", List.class, List.class, int.class, List.class);
        method.setAccessible(true);

        List<String> trainingColumns = List.of("workingYears", "salary", "genre");

        List<Object> expectedWorkingYearsSplits = List.of(1.5);
        List<Object> expectedSalarySplits = List.of(21.75, 25.0, 27.5, 30.625, 36.625);
        List<Object> expectedGenreSplits = List.of("F", "M");

        List<List<Object>> potencialSplits = List.of(expectedWorkingYearsSplits, expectedSalarySplits, expectedGenreSplits);

        int targetColumnIndex = 1; 

        SplitResult result = (SplitResult) method.invoke(tree, trainingColumns, potencialSplits, targetColumnIndex, data);

        assertEquals("genre", result.getBestAttribute());
        assertEquals("F", result.getBestSplitValue());

        for (List<Object> row : result.getBestLeft()) {
            assertEquals("F", row.get(1));
        }

        for (List<Object> row : result.getBestRight()) {
            assertEquals("M", row.get(1));
        }
    }

    @Test
    void testGetBestSplitContinuousColumn() throws Exception {
        var method = DecisionTree.class.getDeclaredMethod("getBestSplit", List.class, List.class, int.class, List.class);
        method.setAccessible(true);

        List<String> trainingColumns = List.of("workingYears", "salary");

        List<Object> expectedWorkingYearsSplits = List.of(1.5);
        List<Object> expectedSalarySplits = List.of(21.75, 25.0, 27.5, 30.625, 36.625);

        List<List<Object>> potencialSplits = List.of(expectedWorkingYearsSplits, expectedSalarySplits);

        int targetColumnIndex = 1; // asumiendo salary está en índice 1

        SplitResult result = (SplitResult) method.invoke(tree, trainingColumns, potencialSplits, targetColumnIndex, data);

        assertEquals("workingYears", result.getBestAttribute());
        assertEquals(1.5, result.getBestSplitValue());

        for (List<Object> row : result.getBestLeft()) {
            assertTrue(((Number) row.get(0)).doubleValue() < 1.5);
        }

        for (List<Object> row : result.getBestRight()) {
            assertTrue(((Number) row.get(0)).doubleValue() >= 1.5);
        }
    }

    @Test
    void testGenerateTreeLimited() throws Exception {
        tree.setTreeMaxDepth(1);

        Node rootNode = tree.generateDecisionTree();
        assertTrue(rootNode.isLeaf());

    }

    @Test
    void testGenerateTree2Layers() throws Exception {
        tree.setTreeMaxDepth(2);

        Node rootNode = tree.generateDecisionTree();
        assertFalse(rootNode.isLeaf());
    }
}