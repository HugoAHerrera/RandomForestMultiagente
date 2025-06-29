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

    public String printTree(HeaderDto datasetHeader) {
        return toTreeString("", datasetHeader);
    }

    private String toTreeString(String indent, HeaderDto datasetHeader) {
        StringBuilder sb = new StringBuilder();

        if (isLeaf) {
            sb.append(indent).append("|--- class: ").append(predictedClass);
        } else {
            String type = datasetHeader.getType(attribute);
            String opLeft = type.equalsIgnoreCase("Continua") ? "<=" : "==";
            String opRight = type.equalsIgnoreCase("Continua") ? ">" : "!=";

            sb.append(indent)
                    .append("|--- ").append(attribute).append(" ").append(opLeft).append(" ").append(value).append("\n");
            if (leftNode != null) {
                sb.append(leftNode.toTreeString(indent + "|   ", datasetHeader)).append("\n");
            }

            sb.append(indent)
                    .append("|--- ").append(attribute).append(" ").append(opRight).append(" ").append(value).append("\n");
            if (rightNode != null) {
                sb.append(rightNode.toTreeString(indent + "|   ", datasetHeader));
            }
        }

        return sb.toString();
    }
}
