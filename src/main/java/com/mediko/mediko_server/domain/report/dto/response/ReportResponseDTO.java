package com.mediko.mediko_server.domain.report.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.openai.domain.Symptom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDTO {

    @JsonProperty("department")
    private Map<String, Object> recommendedDepartment;

    @JsonProperty("possible_conditions")
    private List<Map<String, String>> possibleConditions;

    @JsonProperty("questions_to_doctor")
    private List<Map<String, String>> questionsForDoctor;

    @JsonProperty("symptom_checklist")
    private List<Map<String, Object>> symptomChecklist;

}
