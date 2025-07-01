package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MedicationProcessingState {
    private Long memberId; // ⭐ 소유자 ID 저장

    private Language language;
    private Boolean isSelf;
    private String relation;
    private Gender gender;
    private Integer age;
    private String allergy;
    private String familyHistory;
    private String nowMedicine;
    private String pastHistory;
    private String sign;
    private String sessionId;

    // 필수값 입력 검증
    public boolean isComplete() {
        if (sign == null || sign.isEmpty()) return false;
        if (age == null || gender == null) return false;
        return true;
    }
}
