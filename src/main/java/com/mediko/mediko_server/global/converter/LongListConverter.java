package com.mediko.mediko_server.global.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class LongListConverter implements AttributeConverter<List<Long>, String> {
    private static final String OPEN_BRACKET = "[";
    private static final String CLOSE_BRACKET = "]";
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return OPEN_BRACKET + CLOSE_BRACKET;
        }

        return OPEN_BRACKET + attribute.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(SPLIT_CHAR)) + CLOSE_BRACKET;
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty() || !dbData.startsWith(OPEN_BRACKET) || !dbData.endsWith(CLOSE_BRACKET)) {
            return new ArrayList<>();
        }

        try {
            String content = dbData.substring(1, dbData.length() - 1);
            return Arrays.stream(content.split(SPLIT_CHAR))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}