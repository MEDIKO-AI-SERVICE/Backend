package com.mediko.mediko_server.domain.openai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MedicationTemplateResponseDTO {

    private List<String> medicationNames;

    private Map<String, List<String>> medicationIndications;

    private Map<String, String> medicationImageUrls_1;

    private Map<String, String> medicationImageUrls_2;

    private List<String> questionsForPharmacist;

    private String warningMessage;
}
