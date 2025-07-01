package com.mediko.mediko_server.domain.member.domain.infoType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum Language {
    KO(1), EN(2), VI(3), ZH_CN(4), ZH_TW(5);

    private final int value;

    Language(int value) {
        this.value = value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Language fromString(String value) {
        if (value == null) return null;
        try {
            return Language.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
