package com.mediko.mediko_server.global.converter;

import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter
public class LanguageConverter implements AttributeConverter<Language, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Language language) {
        return language.getValue();
    }

    @Override
    public Language convertToEntityAttribute(Integer dbData) {
        return Arrays.stream(Language.values())
                .filter(e -> e.getValue() == dbData)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown database value:" + dbData));
    }
}
