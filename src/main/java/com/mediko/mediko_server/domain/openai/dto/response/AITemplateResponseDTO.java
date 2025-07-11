package com.mediko.mediko_server.domain.openai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.openai.domain.AITemplate;
import com.mediko.mediko_server.global.converter.StringMapConverter;
import com.mediko.mediko_server.global.converter.StringMapListConverter;
import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AITemplateResponseDTO {

    @JsonProperty("ai_id")
    private Long aiTemplateId;

    @JsonProperty("created_at_kst")
    private String createdAtKst;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("department")
    private String department;

    @JsonProperty("department_description")
    private String departmentDescription;

    @JsonProperty("questions_to_doctor")
    @Convert(converter = StringMapConverter.class)
    private Map<String, String> questionsToDoctor;

    @JsonProperty("symptom_summary")
    private String symptomSummary;

    @JsonProperty("basic_info")
    private List<Map<String, Object>> basicInfo;

    @JsonProperty("health_info")
    private List<Map<String, String>> healthInfo;

    @JsonProperty("image_info")
    private List<Map<String, String>> fileInfo;

    // AI 증상 분석 응답 변환
    public AITemplateResponseDTO convertToAnalysisResponse() {
        return AITemplateResponseDTO.builder()
                .aiTemplateId(this.aiTemplateId)
                .department(this.department)
                .departmentDescription(this.departmentDescription)
                .questionsToDoctor(this.questionsToDoctor)
                .build();
    }

    // AI 증상 요약 응답 변환
    public AITemplateResponseDTO convertToSummaryResponse() {
        return AITemplateResponseDTO.builder()
                .aiTemplateId(this.aiTemplateId)
                .basicInfo(this.basicInfo)
                .healthInfo(this.healthInfo)
                .symptomSummary(this.symptomSummary)
                .fileInfo(this.fileInfo)
                .build();
    }

    public static AITemplateResponseDTO fromEntity(
            AITemplate aiTemplate,
            List<Map<String, Object>> basicInfo,
            List<Map<String, String>> healthInfo,
            List<Map<String, String>> fileInfo
    ) {
        return AITemplateResponseDTO.builder()
                .aiTemplateId(aiTemplate.getId())
                .createdAtKst(aiTemplate.getCreatedAtKst())
                .summary(aiTemplate.getSummary())
                .department(aiTemplate.getDepartment())
                .departmentDescription(aiTemplate.getDepartmentDescription())
                .questionsToDoctor(aiTemplate.getQuestionsToDoctor())
                .symptomSummary(aiTemplate.getSymptomSummary())
                .basicInfo(basicInfo)
                .healthInfo(healthInfo)
                .fileInfo(fileInfo)
                .build();
    }

}
