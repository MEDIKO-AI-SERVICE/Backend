package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.openai.domain.Intensity;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentProcessingState {
    private Long memberId;

    private String sessionId;

    private String sign;

    private String startDate;

    private Intensity intensity;

    private Boolean complete;

    public boolean isComplete() {
        return sign != null && !sign.isBlank()
                && startDate != null && !startDate.isBlank()
                && intensity != null;
    }
}
