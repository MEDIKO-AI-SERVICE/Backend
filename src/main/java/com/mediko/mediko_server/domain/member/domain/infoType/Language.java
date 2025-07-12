package com.mediko.mediko_server.domain.member.domain.infoType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum Language {
    KO(1),    // 한국어
    EN(2),    // 영어
    VI(3),    // 베트남어
    ZH_CN(4), //중국어 간체
    ZH_TW(5), //중국어 번체
    NE(6),    // 네팔어
    ID(7),    // 인도네시아어
    TH(8);    // 태국어

    private final int value;

    Language(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
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
