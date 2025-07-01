package com.randomforest.randomforest;

import lombok.Getter;
import lombok.Setter;

import com.randomforest.dto.HeaderDto;

@Getter
@Setter
public class Node {
    /**
     * Column value used in this tree node
     */
    private String attribute;

    /**
     * == for categorical features, >= for continuous features
     */
    private String operation;

    /**
     * Specific value to split the data, can be a number or a string
     */
    private Object value;

    /**
     * Left child node
     */
    private Node leftNode;

    /**
     * Right child node
     */
    private Node rightNode;

    /**
     * True if it is a final node, False if not
     */
    private boolean isLeaf;

    /**
     * Column value, for final nodes
     */
    private Object predictedClass;

    /**
     * Constructor for creating an intermediate tree node with a splitting condition.
     * 
     * @param attribute The attribute name on which the split is made
     * @param operation The comparison operation ("==", "<=")
     * @param value The value to compare the attribute against
     */
    public Node(String attribute, String operation, Object value) {
        this.attribute = attribute;
        this.operation = operation;
        this.value = value;
        this.isLeaf = false;
    }

    /**
     * Constructor for creating a final tree node.
     * 
     * @param predictedClass column value
     */
    public Node(Object predictedClass) {
        this.isLeaf = true;
        this.predictedClass = predictedClass;
    }

    /**
     * Method to get if a Node is final or not
     */
    public boolean isLeaf() {
        return isLeaf;
    }
}
