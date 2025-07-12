package com.mediko.mediko_server.domain.openai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.DepartmentTemplate;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentTemplateRequestDTO {

    @JsonProperty("body_part")
    private String bodyPart;

    private List<String> selectedSign;

    private SymptomRequest_2DTO symptom;

    public DepartmentTemplate toEntity(Member member, String sessionId) {
        return DepartmentTemplate.builder()
                .bodyPart(this.bodyPart)
                .selectedSign(this.selectedSign)
                .startDate(this.symptom.getStartDate())
                .intensity(this.symptom.getIntensity())
                .additional(this.symptom.getAdditional())
                .member(member)
                .sessionId(sessionId)
                .build();
    }
}
