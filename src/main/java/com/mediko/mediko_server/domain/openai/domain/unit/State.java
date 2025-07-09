package com.mediko.mediko_server.domain.openai.domain.unit;

import lombok.Getter;

@Getter
public enum State {
    WORSE("심해졌어요"),
    BETTER("나아졌어요"),
    SAME("그대로에요");

    private final String description;

    State(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static State fromDescription(String description) {
        for (State state : State.values()) {
            if (state.description.equals(description)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown description: " + description);
    }
}
