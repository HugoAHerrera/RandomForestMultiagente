package com.randomforest.randomforest;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SplitResult {
    /**
     * Column name used to split the dataset
     */
    private String bestAttribute;
    
    /**
     * Value used to split the dataset, can be a number of a string
     */
    private Object bestSplitValue;

    /**
     * Dataset that surpass the split value
     */
    private List<List<Object>> bestLeft;

    /**
     * Remaining rows of the original dataset
     */
    private List<List<Object>> bestRight;
}
