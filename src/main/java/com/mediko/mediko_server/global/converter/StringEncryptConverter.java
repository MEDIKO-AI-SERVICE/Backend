package com.mediko.mediko_server.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Converter
public class StringEncryptConverter implements AttributeConverter<String, String> {

    private final StringEncryptor encryptor;

    @Autowired
    public StringEncryptConverter(@Qualifier("jasyptStringEncryptor") StringEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        return "ENC(" + encryptor.encrypt(attribute) + ")";
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        if (dbData.startsWith("ENC(") && dbData.endsWith(")")) {
            String encryptedValue = dbData.substring(4, dbData.length() - 1);
            return encryptor.decrypt(encryptedValue);
        }
        return dbData;
    }
}

