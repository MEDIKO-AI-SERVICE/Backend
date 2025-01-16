package com.mediko.mediko_server.domain.openai.domain;

import lombok.Getter;

@Getter
public enum TimeUnit {
    DEFAULT(0, 0),
    HOUR(1, 23),
    DAY(1, 6),
    WEEK(1, 3),
    MONTH(1, 11),
    YEAR(1, 90);

    private final int minValue;
    private final int maxValue;

    TimeUnit(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public boolean isValidValue(int value) {
        return value >= minValue && value <= maxValue;
    }
}