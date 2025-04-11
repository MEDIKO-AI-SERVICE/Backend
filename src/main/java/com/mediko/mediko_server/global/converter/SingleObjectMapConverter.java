package com.mediko.mediko_server.global.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Converter
public class SingleObjectMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StringEncryptor encryptor;

    @Autowired
    public SingleObjectMapConverter(@Qualifier("jasyptStringEncryptor") StringEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            if (attribute == null) return null;
            String jsonValue = objectMapper.writeValueAsString(attribute);
            return "ENC(" + encryptor.encrypt(jsonValue) + ")";
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null) return new HashMap<>();

            String decrypted;
            if (dbData.startsWith("ENC(") && dbData.endsWith(")")) {
                String encryptedValue = dbData.substring(4, dbData.length() - 1);
                decrypted = encryptor.decrypt(encryptedValue);
            } else {
                decrypted = dbData;
            }

            return objectMapper.readValue(decrypted, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }
}