package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.openai.domain.Symptom;
import com.mediko.mediko_server.domain.openai.domain.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DurationResponseDTO {

    private Integer durationValue;

    private TimeUnit durationUnit;

    private Long symptomId;

    public static DurationResponseDTO fromEntity(Symptom symptom) {
        return new DurationResponseDTO(
                symptom.getDurationValue(),
                symptom.getDurationUnit(),
                symptom.getId()
        );
    }
}
