package com.mediko.mediko_server.domain.openai.application.processingState;

import com.mediko.mediko_server.domain.openai.domain.unit.Intensity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentProcessingState {

    private Long memberId;

    private String sessionId;

    private String bodyPart;

    private List<String> selectedSign;

    private Intensity intensity;

    private String startDate;

    private String additional;

    private Boolean complete;

    public boolean isComplete() {
        return bodyPart != null && !bodyPart.isBlank()
                && selectedSign != null && !selectedSign.isEmpty()
                && startDate != null && !startDate.isBlank()
                && intensity != null;
    }
}
