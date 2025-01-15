package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.openai.domain.Symptom;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdditionalInfoResponseDTO {

    private String additional;

    private Long symptomId;

    //private List<String> imageUrls;

    public static AdditionalInfoResponseDTO fromEntity(Symptom symptom) {
        return new AdditionalInfoResponseDTO(
                symptom.getAdditional(),
                symptom.getId()
        );
    }
}
