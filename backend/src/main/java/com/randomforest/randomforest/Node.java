package com.randomforest.randomforest;

import lombok.Getter;
import lombok.Setter;

import com.randomforest.dto.HeaderDto;

@Getter
@Setter
public class Node {
    private String attribute;
    private String operation;
    private Object value;
    private Node leftNode;
    private Node rightNode;
    private boolean isLeaf;
    private Object predictedClass;

    public Node(String attribute, String operation, Object value) {
        this.attribute = attribute;
        this.operation = operation;
        this.value = value;
        this.isLeaf = false;
    }

    public Node(Object predictedClass) {
        this.isLeaf = true;
        this.predictedClass = predictedClass;
    }

    public boolean isLeaf() {
        return isLeaf;
    }
}
