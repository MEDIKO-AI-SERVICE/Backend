package com.mediko.mediko_server.domain.openai.domain.unit;

public enum Intensity {
    MILD("통증이 약해요"),
    LIGHT("통증이 가벼워요"),
    UNCOMFORTABLE("통증이 불편해요"),
    HARD_TO_BEAR("통증을 참기 어려워요"),
    STRONG("통증이 강해요"),
    SEVERE("통증이 심해요");

    private final String description;

    Intensity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Intensity fromDescription(String desc) {
        for (Intensity intensity : Intensity.values()) {
            if (intensity.description.equals(desc)) {
                return intensity;
            }
        }
        throw new IllegalArgumentException("Unknown intensity: " + desc);
    }

    @Override
    public String toString() {
        return name();
    }
}
