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

    private Gender gender;     // Step 1

    private Integer age;       // Step 2

    private String allergy;    // Step 3

    private String familyHistory; // Step 4

    private String nowMedicine;    // Step 5

    private String pastHistory;   // Step 6
}
