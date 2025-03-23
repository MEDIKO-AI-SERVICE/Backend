package com.mediko.mediko_server.domain.member.domain.infoType;

import java.util.Locale;

public enum Language {
    KO,             // 한국어
    EN,             // 영어
    VI,             // 베트남어
    ZH_CN,          // 중국어 간체
    ZH_TW;          // 중국어 번체

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
