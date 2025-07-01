package com.randomforest.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeaderDto implements Serializable {
    private Map<String, String> types;

    public String getType(String attribute) {
        return types.getOrDefault(attribute, "unknown");
    }
}
