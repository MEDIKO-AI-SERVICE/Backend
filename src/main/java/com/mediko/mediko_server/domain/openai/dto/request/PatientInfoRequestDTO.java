package com.mediko.mediko_server.domain.openai.dto.request;

import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PatientInfoRequestDTO {

    private Integer height;

    private Integer weight;

    private Gender gender;

    private Integer age;

    private String allergy;

    private String familyHistory;

    private String nowMedicine;

    private String pastHistory;

}
