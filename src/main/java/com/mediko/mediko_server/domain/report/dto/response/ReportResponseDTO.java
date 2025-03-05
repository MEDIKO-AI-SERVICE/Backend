package com.mediko.mediko_server.domain.report.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.report.domain.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDTO {

    @JsonProperty("report_id")
    private Long reportId;

    @JsonProperty("department")
    private Map<String, Object> recommendedDepartment;

    @JsonProperty("possible_conditions")
    private List<Map<String, String>> possibleConditions;

    @JsonProperty("questions_to_doctor")
    private List<Map<String, String>> questionsForDoctor;

    @JsonProperty("symptom_checklist")
    private List<Map<String, Object>> symptomChecklist;

    @JsonProperty("basic_info")
    private List<Map<String, Object>> basicInfo;

    @JsonProperty("health_info")
    private List<Map<String, String>> healthInfo;

    @JsonProperty("body_info")
    private List<Map<String, Object>> bodyInfo;

    @JsonProperty("symptom_info")
    private List<Map<String, String>> symptomInfo;

    @JsonProperty("image_info")
    private List<Map<String, String>> fileInfo;


    public static ReportResponseDTO fromEntity(
            Report report, List<Map<String, Object>> basicInfo,
            List<Map<String, String>> healthInfo, List<Map<String, Object>> bodyInfo,
            List<Map<String, String>> symptomInfo, List<Map<String, String>> imgInfo) {
        return new ReportResponseDTO(
                report.getId(),
                report.getRecommendedDepartment(),
                report.getPossibleConditions(),
                report.getQuestionsForDoctor(),
                report.getSymptomChecklist(),
                basicInfo,
                healthInfo,
                bodyInfo,
                symptomInfo,
                imgInfo
        );
    }
}
