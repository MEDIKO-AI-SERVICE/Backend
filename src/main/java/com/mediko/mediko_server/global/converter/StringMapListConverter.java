package com.mediko.mediko_server.global.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Converter
public class StringMapListConverter implements AttributeConverter<List<Map<String, Object>>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Map<String, Object>> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @Override
    public List<Map<String, Object>> convertToEntityAttribute(String dbData) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametricType(
                    List.class,
                    objectMapper.getTypeFactory().constructParametricType(
                            Map.class,
                            String.class,
                            Object.class
                    )
            );

            if (dbData == null || dbData.isEmpty()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(dbData, type);
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }
}