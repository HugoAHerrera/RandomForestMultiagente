package com.randomforest.randomforest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class NodeTest {
    @Test
    void testConstructorNonLeafNode() {
        Node node = new Node("age", "<=", 30);

        assertFalse(node.isLeaf());
        assertEquals("age", node.getAttribute());
        assertEquals("<=", node.getOperation());
        assertEquals(30, node.getValue());
        assertNull(node.getLeftNode());
        assertNull(node.getRightNode());
        assertNull(node.getPredictedClass());
    }

    @Test
    void testConstructorLeafNode() {
        Node leaf = new Node("classA");

        assertTrue(leaf.isLeaf());
        assertEquals("classA", leaf.getPredictedClass());
        assertNull(leaf.getAttribute());
        assertNull(leaf.getOperation());
        assertNull(leaf.getValue());
        assertNull(leaf.getLeftNode());
        assertNull(leaf.getRightNode());
    }

    @Test
    void testSettersAndGetters() {
        Node node = new Node("feature", ">", 10);

        node.setLeftNode(new Node("leftClass"));
        node.setRightNode(new Node("rightClass"));
        node.setPredictedClass("predictedClass");
        node.setLeaf(true);
        node.setAttribute("newAttribute");
        node.setOperation("==");
        node.setValue(5);

        assertEquals("newAttribute", node.getAttribute());
        assertEquals("==", node.getOperation());
        assertEquals(5, node.getValue());
        assertTrue(node.isLeaf());
        assertEquals("predictedClass", node.getPredictedClass());
        assertNotNull(node.getLeftNode());
        assertNotNull(node.getRightNode());
        assertEquals("leftClass", node.getLeftNode().getPredictedClass());
        assertEquals("rightClass", node.getRightNode().getPredictedClass());
    }
}
