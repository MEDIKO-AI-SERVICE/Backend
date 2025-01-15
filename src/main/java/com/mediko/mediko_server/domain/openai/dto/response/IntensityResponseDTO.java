package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.openai.domain.Symptom;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntensityResponseDTO {

    private Integer intensity;

    private Long symptomId;

    public static IntensityResponseDTO fromEntity(Symptom symptom) {
        return new IntensityResponseDTO(
                symptom.getIntensity(),
                symptom.getId()
        );
    }
}
