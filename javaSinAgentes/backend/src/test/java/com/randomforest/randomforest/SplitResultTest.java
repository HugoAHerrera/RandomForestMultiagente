package com.randomforest.randomforest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

public class SplitResultTest {
    @Test
    void testConstructor() {
        String attribute = "age";
        Object splitValue = 30;

        List<List<Object>> left = new ArrayList<>();
        left.add(List.of("Juan", 25));
        left.add(List.of("Ana", 28));

        List<List<Object>> right = new ArrayList<>();
        right.add(List.of("Luis", 35));
        right.add(List.of("Maria", 40));

        SplitResult result = new SplitResult(attribute, splitValue, left, right);

        assertEquals(attribute, result.getBestAttribute());
        assertEquals(splitValue, result.getBestSplitValue());
        assertEquals(left, result.getBestLeft());
        assertEquals(right, result.getBestRight());
    }

    @Test
    void testEmptyLists() {
        SplitResult result = new SplitResult("atributo", "valor", List.of(), List.of());

        assertNotNull(result.getBestLeft());
        assertNotNull(result.getBestRight());
        assertTrue(result.getBestLeft().isEmpty());
        assertTrue(result.getBestRight().isEmpty());
    }
}
