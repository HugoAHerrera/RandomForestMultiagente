package com.randomforest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.stream.Collectors;
import java.io.Serializable;

import java.util.Map;

@Getter
@Setter
public class PredictionRequestDto implements Serializable {
    private String name;
    private String target;
    private String task;
    private String userName;
    private String fileName;
    private Map<String, String> features;
}