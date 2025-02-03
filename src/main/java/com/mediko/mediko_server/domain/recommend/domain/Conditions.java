package com.mediko.mediko_server.domain.recommend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Conditions {
    PREMATURE_MOTHER(1),    //조산 산모
    PSYCHIATRIC_PATIENT(2), //정신질환자
    NEWBORN(3),             //신생아
    SEVERE_BURN(4);         //중증 화상

    private final int code;

}
