package com.mediko.mediko_server.domain.recommend.domain.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum Condition {
    PREMATURE_MOTHER("조산산모"),
    PSYCHIATRIC_PATIENT("정신질환자"),
    NEWBORN("신생아"),
    SEVERE_BURN("중증화상");

    private final String description;

}
