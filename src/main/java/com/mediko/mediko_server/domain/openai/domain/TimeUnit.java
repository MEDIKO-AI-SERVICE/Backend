package com.mediko.mediko_server.domain.openai.domain;

import lombok.Getter;
import java.util.Arrays;
import java.util.List;

@Getter
public enum TimeUnit {
    DEFAULT(0, Arrays.asList(0)),
    MINUTE(5, Arrays.asList(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)),
    HOUR(5, Arrays.asList(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)),
    DAY(5, Arrays.asList(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)),
    WEEK(5, Arrays.asList(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)),
    MONTH(5, Arrays.asList(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)),
    YEAR(5, Arrays.asList(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55));

    private final int minValue;
    private final List<Integer> validValues;

    TimeUnit(int minValue, List<Integer> validValues) {
        this.minValue = minValue;
        this.validValues = validValues;
    }
}