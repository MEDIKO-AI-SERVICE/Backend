package com.mediko.mediko_server.domain.openai.application.processingState;

import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.domain.unit.Intensity;
import com.mediko.mediko_server.domain.openai.domain.unit.State;
import com.mediko.mediko_server.domain.openai.domain.unit.TimeUnit;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AIProcessingState {
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

    private String bodyPart;

    private List<String> selectedSign;

    private Intensity intensity;

    private LocalDate startDate;

    private Integer durationValue;

    private TimeUnit durationUnit;

    private State state;

    private String additional;

    private Boolean complete;

    // 필수값 입력 검증
    public boolean isComplete() {
        if (selectedSign == null || selectedSign.isEmpty()) return false;
        if (age == null || gender == null) return false;
        if (intensity == null) return false;
        if (startDate == null) return false;
        if (durationValue == null) return false;
        if (durationUnit == null) return false;
        if (state == null) return false;
        return true;
    }

}
