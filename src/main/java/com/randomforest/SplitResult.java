package com.randomforest;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SplitResult {
    private String bestAttribute;
    private Object bestSplitValue;
    private List<List<Object>> bestLeft;
    private List<List<Object>> bestRight;
}
