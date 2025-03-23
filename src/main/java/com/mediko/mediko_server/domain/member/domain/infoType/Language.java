package com.mediko.mediko_server.domain.member.domain.infoType;

import java.util.Locale;

public enum Language {
    KO(1),             // 한국어
    EN(2),             // 영어
    VI(3),             // 베트남어
    ZH_CN(4),          // 중국어 간체
    ZH_TW(5);          // 중국어 번체

    private final int value;

    Language(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Language fromLocale(Locale locale) {
        String lang = locale.getLanguage();
        String country = locale.getCountry();

        return switch (lang) {
            case "ko" -> KO;
            case "en" -> EN;
            case "vi" -> VI;
            case "zh" -> {
                if ("TW".equals(country)) yield ZH_TW;
                else yield ZH_CN;
            }
            default -> KO;
        };
    }
}
