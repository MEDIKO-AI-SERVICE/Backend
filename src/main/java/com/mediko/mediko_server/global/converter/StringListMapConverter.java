package com.mediko.mediko_server.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Converter
public class StringListMapConverter implements AttributeConverter<Map<String, List<String>>, String> {
    private static final String OPEN_BRACKET = "{";
    private static final String CLOSE_BRACKET = "}";
    private static final String KEY_VALUE_SEPARATOR = ":";
    private static final String ENTRY_SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(Map<String, List<String>> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return OPEN_BRACKET + CLOSE_BRACKET;
        }

        return attribute.entrySet().stream()
                .map(entry -> entry.getKey() + KEY_VALUE_SEPARATOR + listToString(entry.getValue()))
                .collect(Collectors.joining(ENTRY_SEPARATOR, OPEN_BRACKET, CLOSE_BRACKET));
    }

    @Override
    public Map<String, List<String>> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty() || !dbData.startsWith(OPEN_BRACKET) || !dbData.endsWith(CLOSE_BRACKET)) {
            return new HashMap<>();
        }

        try {
            String content = dbData.substring(1, dbData.length() - 1);
            String[] entries = content.split(ENTRY_SEPARATOR);
            Map<String, List<String>> map = new HashMap<>();
            for (String entry : entries) {
                String[] keyValue = entry.split(KEY_VALUE_SEPARATOR);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    List<String> value = stringToList(keyValue[1].trim());
                    map.put(key, value);
                }
            }
            return map;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private String listToString(List<String> list) {
        return list.stream()
                .collect(Collectors.joining(",", "[", "]"));
    }

    private List<String> stringToList(String str) {
        if (str == null || str.isEmpty()) {
            return List.of();
        }
        return List.of(str.substring(1, str.length() - 1).split(","));
    }

}
