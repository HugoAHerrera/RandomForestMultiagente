package com.randomforest.randomforest;

import com.randomforest.dto.HeaderDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BootstrappingTest {
    private List<List<Object>> data;
    private int sampleSize;

    @BeforeEach
    void setUp() {
        data = new ArrayList<>();
        data.add(List.of(1, 20.5, "M"));
        data.add(List.of(1, 33.25, "M"));
        data.add(List.of(2, 28, "F"));
        data.add(List.of(1, 23, "F"));
        data.add(List.of(1, 27, "M"));
        data.add(List.of(2, 40, "F"));

        sampleSize = 6;
    }


    @Test
    void testGetCandidateAttribs(){
        HeaderDto headerDto = new HeaderDto();
        Map<String, String> columnTypes = new HashMap<>();
        columnTypes.put("petal_length", "Continua");
        columnTypes.put("petal_width", "Continua");
        columnTypes.put("sepal_width", "Continua");
        columnTypes.put("sepal_length", "Continua");
        columnTypes.put("species", "Categórica");

        headerDto.setTypes(columnTypes);

        List<String> expectedCandidateAttribs = List.of("petal_length", "petal_width", "sepal_width", "sepal_length");
        List<String> actualList = Bootstrapping.getCandidateAttributes(headerDto, "species");
        
        assertTrue(actualList.containsAll(expectedCandidateAttribs) && expectedCandidateAttribs.containsAll(actualList));
    }

    @Test
    void testGetTrainingElementsSize() {
        List<List<List<Object>>> result = Bootstrapping.getTrainingElements(data, sampleSize);
        List<List<Object>> training = result.get(0);

        assertEquals(sampleSize, training.size());
    }

    @Test
    void testGetTrainingElementsContainAllRows() {
        List<List<List<Object>>> result = Bootstrapping.getTrainingElements(data, sampleSize);
        List<List<Object>> training = result.get(0);
        List<List<Object>> test = result.get(1);

        // Check that training and testing datasets cotains all the original dataset rows
        List<List<Object>> combined = new ArrayList<>();
        combined.addAll(training);
        combined.addAll(test);

        assertTrue(combined.containsAll(data));
    }

    @Test
    void testGetTrainingElementsDifferentSamples() {
        List<List<List<Object>>> previousTrainings = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            List<List<List<Object>>> result = Bootstrapping.getTrainingElements(data, sampleSize);
            List<List<Object>> training = result.get(0);
            previousTrainings.add(training);
        }

        boolean differentTrainingFound = false;
        for (int i = 0; i < previousTrainings.size(); i++) {
            for (int j = i + 1; j < previousTrainings.size(); j++) {
                if (!new HashSet<>(previousTrainings.get(i)).equals(new HashSet<>(previousTrainings.get(j)))) {
                    differentTrainingFound = true;
                    break;
                }
            }
        }
        assertTrue(differentTrainingFound);
    }
}