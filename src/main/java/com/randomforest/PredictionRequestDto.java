package com.randomforest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PredictionRequestDto {
    private String name;
    private String target;
    private String task;
    private Map<String, String> features;
}