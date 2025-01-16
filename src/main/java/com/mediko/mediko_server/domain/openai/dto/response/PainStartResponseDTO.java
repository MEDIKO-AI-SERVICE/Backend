package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.openai.domain.Symptom;
import com.mediko.mediko_server.domain.openai.domain.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PainStartResponseDTO {

    private Integer startValue;

    private TimeUnit startUnit;

    private Long symptomId;

    public static PainStartResponseDTO fromEntity(Symptom symptom) {
        return new PainStartResponseDTO(
                symptom.getStartValue(),
                symptom.getStartUnit(),
                symptom.getId()
        );
    }
}
