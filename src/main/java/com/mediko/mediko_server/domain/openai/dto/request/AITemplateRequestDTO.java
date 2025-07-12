package com.mediko.mediko_server.domain.openai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.AITemplate;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AITemplateRequestDTO {

    private boolean isSelf;

    @JsonProperty("bodypart")
    private String bodyPart;

    private List<String> selectedSign;

    @JsonProperty("patientinfo")
    private PatientInfoRequestDTO patientInfo;

    private SymptomRequest_1DTO symptom;

    public AITemplate toEntity(Member member, String sessionId) {
        SymptomRequest_1DTO symptom = this.symptom;
        return AITemplate.builder()
                .isSelf(this.isSelf)
                .bodyPart(this.bodyPart)
                .selectedSign(this.selectedSign)
                .intensity(symptom.getIntensity())
                .startDate(symptom.getStartDate())
                .durationValue(symptom.getDurationValue())
                .durationUnit(com.mediko.mediko_server.domain.openai.domain.unit.
                        TimeUnit.valueOf(symptom.getDurationUnit().name()))
                .state(symptom.getState())
                .additional(symptom.getAdditional())
                .member(member)
                .sessionId(sessionId)
                .build();
    }
}
