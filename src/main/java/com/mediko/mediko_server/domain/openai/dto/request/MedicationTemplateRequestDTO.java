package com.mediko.mediko_server.domain.openai.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MedicationTemplateRequestDTO {

    private boolean isSelf;

    private PatientInfoRequestDTO patientInfo;

    private String sign;
}
