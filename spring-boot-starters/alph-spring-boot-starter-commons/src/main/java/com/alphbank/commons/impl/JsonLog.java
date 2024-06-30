package com.alphbank.commons.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonLog {

    private final ObjectMapper objectMapper;

    public JsonLog(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String format(Object object) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return object.toString();
        }
    }
}
