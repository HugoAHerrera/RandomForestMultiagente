package com.randomforest.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class PredictionResultDto {
    private String userName;
    private Map<String, String> target;
    private String task;
    private Map<String, String> features;
    private String fileName;
    private double accuracy;
}