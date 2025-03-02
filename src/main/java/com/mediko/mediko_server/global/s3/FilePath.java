package com.mediko.mediko_server.global.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FilePath {
    PROFILE("profile-images"),
    SYMPTOM("symptom");

    private final String path;
}
