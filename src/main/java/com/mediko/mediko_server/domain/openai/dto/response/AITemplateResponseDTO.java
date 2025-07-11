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

import java.util.HashMap;
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
    public Map<String, Object> toSummaryMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("ai_id", this.aiTemplateId);
        map.put("basic_info", this.basicInfo);
        map.put("health_info", this.healthInfo);
        map.put("symptom_summary", this.symptomSummary);
        map.put("image_info", this.fileInfo);
        // 필요한 필드만 추가
        return map;
    }

    // AI 증상 분석 응답 변환
    public Map<String, Object> toAnalysisMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("ai_id", this.aiTemplateId);
        map.put("department", this.department);
        map.put("department_description", this.departmentDescription);
        map.put("questions_to_doctor", this.questionsToDoctor);
        return map;
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
