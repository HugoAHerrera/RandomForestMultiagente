package com.randomforest.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeaderDto implements Serializable {
    private Map<String, String> types;

    /**
     * Returns the type of a given attribute.
     *
     * @param attribute attribute name
     * @return The type as a String if found or unknown if not
     */
    public String getType(String attribute) {
        return types.getOrDefault(attribute, "unknown");
    }
}
