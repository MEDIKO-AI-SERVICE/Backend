package com.mediko.mediko_server.domain.openai.application.processingState;

import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MedicationProcessingState {

    private Long memberId;

    private String sessionId;

    private Boolean isSelf;

    private String relation;

    private Gender gender;

    private Integer age;

    private String allergy;

    private String familyHistory;

    private String nowMedicine;

    private String pastHistory;

    private String sign;

    private Boolean complete;

    // 필수값 입력 검증
    public boolean isComplete() {
        if (sign == null || sign.isEmpty()) return false;
        if (age == null || gender == null) return false;
        return true;
    }
}
