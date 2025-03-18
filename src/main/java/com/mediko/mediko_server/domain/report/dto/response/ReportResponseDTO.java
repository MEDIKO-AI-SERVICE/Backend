package com.mediko.mediko_server.domain.report.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.report.domain.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    // 환자용 응답 변환
    public ReportResponseDTO convertToPatientResponse() {
        return ReportResponseDTO.builder()
                .reportId(this.reportId)
                .recommendedDepartment(this.recommendedDepartment)
                .possibleConditions(this.possibleConditions)
                .questionsForDoctor(this.questionsForDoctor)
                .symptomChecklist(this.symptomChecklist)
                .build();
    }

    // 의사용 응답 변환
    public ReportResponseDTO convertToDoctorResponse() {
        return ReportResponseDTO.builder()
                .reportId(this.reportId)
                .basicInfo(this.basicInfo)
                .healthInfo(this.healthInfo)
                .bodyInfo(this.bodyInfo)
                .symptomInfo(this.symptomInfo)
                .possibleConditions(this.possibleConditions)
                .fileInfo(this.fileInfo)
                .build();
    }

    // fromEntity 메서드는 수동으로 작성해야 함
    public static ReportResponseDTO fromEntity(
            Report report, List<Map<String, Object>> basicInfo,
            List<Map<String, String>> healthInfo, List<Map<String, Object>> bodyInfo,
            List<Map<String, String>> symptomInfo, List<Map<String, String>> imgInfo) {
        return ReportResponseDTO.builder()
                .reportId(report.getId())
                .recommendedDepartment(report.getRecommendedDepartment())
                .possibleConditions(report.getPossibleConditions())
                .questionsForDoctor(report.getQuestionsForDoctor())
                .symptomChecklist(report.getSymptomChecklist())
                .basicInfo(basicInfo)
                .healthInfo(healthInfo)
                .bodyInfo(bodyInfo)
                .symptomInfo(symptomInfo)
                .fileInfo(imgInfo)
                .build();
    }
}

