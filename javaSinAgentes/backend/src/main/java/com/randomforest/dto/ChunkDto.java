package com.randomforest.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChunkDto implements Serializable {
    private Map<String, String> types;
    private List<Map<String, String>> rows;
}