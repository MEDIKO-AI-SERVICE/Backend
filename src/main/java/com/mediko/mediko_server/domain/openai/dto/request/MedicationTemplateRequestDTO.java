package com.mediko.mediko_server.domain.openai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.domain.MedicationTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MedicationTemplateRequestDTO {

    private Language language;

    private boolean isSelf;

    @JsonProperty("patient_info")
    private PatientInfoRequestDTO patientInfo;

    private String sign;

    public MedicationTemplate toEntity(Member member, String sessionId) {
        return MedicationTemplate.builder()
                .isSelf(this.isSelf)
                .sign(this.sign)
                .member(member)
                .sessionId(sessionId)
                .build();
    }
}
