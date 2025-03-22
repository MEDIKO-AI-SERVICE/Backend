package com.mediko.mediko_server.domain.translation.domain.repository;

public enum TranslationType {
    MAIN_BODY_PART("주요 신체 부위"),
    SUB_BODY_PART("세부 신체 부위"),
    DETAILED_SIGN("상세 증상");

    private final String description;

    TranslationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
