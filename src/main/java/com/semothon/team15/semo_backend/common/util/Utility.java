package com.semothon.team15.semo_backend.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Utility {

    private final ObjectMapper objectMapper;

    public String toJsonString(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public Map<String, Object> toMap(String jsonStr) {
        try {
            return objectMapper.readValue(jsonStr, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}