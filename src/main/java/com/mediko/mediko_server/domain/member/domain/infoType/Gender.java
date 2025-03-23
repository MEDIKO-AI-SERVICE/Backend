package com.mediko.mediko_server.domain.member.domain.infoType;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE("남성", "NAM", "男性"),
    FEMALE("여성", "NỮ", "女性");

    private final String[] values;

    Gender(String... values) {
        this.values = values;
    }

    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.toLowerCase().trim();

        for (Gender gender : Gender.values()) {
            for (String acceptedValue : gender.values) {
                if (normalizedValue.equals(acceptedValue.toLowerCase())) {
                    return gender;
                }
            }
            if (normalizedValue.equals(gender.name().toLowerCase())) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid gender value: " + value);
    }
}
