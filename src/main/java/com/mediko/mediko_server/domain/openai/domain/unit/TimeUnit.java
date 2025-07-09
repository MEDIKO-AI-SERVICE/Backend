package com.mediko.mediko_server.domain.openai.domain.unit;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public enum TimeUnit {
    MINUTE(1, generateRange(1, 59)),
    HOUR(1, generateRange(1, 23)),
    DAY(1, generateRange(1, 31)),
    WEEK(1, generateRange(1, 52)),
    MONTH(1, generateRange(1, 12)),
    YEAR(1, generateRange(1, 100));

    private final int minValue;
    private final List<Integer> validValues;

    TimeUnit(int minValue, List<Integer> validValues) {
        this.minValue = minValue;
        this.validValues = validValues;
    }

    private static List<Integer> generateRange(int start, int end) {
        return IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
    }
}
