package com.mediko.mediko_server.domain.report.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.report.domain.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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


    // value와 unit을 합쳐서 출력
    public static List<Map<String, String>> mergeSymptomInfo(
            List<Map<String, String>> symptomInfo) {

        return symptomInfo.stream().map(info -> {
            Map<String, String> mergedInfo = new HashMap<>(info);

            String start = info.get("start_value") + " " + info.get("start_unit");
            String duration = info.get("duration_value") + " " + info.get("duration_unit");

            mergedInfo.put("start", start);
            mergedInfo.put("duration", duration);

            mergedInfo.remove("start_value");
            mergedInfo.remove("start_unit");
            mergedInfo.remove("duration_value");
            mergedInfo.remove("duration_unit");

            return mergedInfo;
        }).collect(Collectors.toList());
    }




    private Map<String, Object> filterByLanguage(Map<String, Object> data, String language) {
        if (data == null || data.get(language) == null) return new HashMap<>();
        Map<String, Object> filtered = new HashMap<>();
        filtered.put(language, data.get(language));
        return filtered;
    }

    private List<Map<String, String>> filterConditionsByLanguage(List<Map<String, String>> conditions, String language) {
        if (conditions == null) return new ArrayList<>();
        return conditions.stream()
                .filter(condition -> condition != null && condition.get(language) != null)
                .map(condition -> {
                    Map<String, String> filtered = new HashMap<>();
                    filtered.put(language, condition.get(language));
                    return filtered;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, String>> filterQuestionsByLanguage(List<Map<String, String>> questions, String language) {
        if (questions == null) return new ArrayList<>();
        return questions.stream()
                .filter(question -> question != null && question.get(language) != null)
                .map(question -> {
                    Map<String, String> filtered = new HashMap<>();
                    filtered.put(language, question.get(language));
                    return filtered;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> filterChecklistByLanguage(List<Map<String, Object>> checklist, String language) {
        if (checklist == null) return new ArrayList<>();

        return checklist.stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    Map<String, Object> filteredItem = new HashMap<>();

                    // condition 처리
                    @SuppressWarnings("unchecked")
                    Map<String, Object> condition = (Map<String, Object>) item.get("condition");
                    if (condition != null && condition.get(language) != null) {
                        Map<String, Object> filteredCondition = new HashMap<>();
                        filteredCondition.put(language, condition.get(language));
                        filteredItem.put("condition", filteredCondition);
                    }

                    // symptoms 처리
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> symptoms = (List<Map<String, Object>>) item.get("symptoms");
                    if (symptoms != null) {
                        List<Map<String, String>> filteredSymptoms = symptoms.stream()
                                .filter(symptom -> symptom != null && symptom.get(language) != null)
                                .map(symptom -> {
                                    Map<String, String> filteredSymptom = new HashMap<>();
                                    filteredSymptom.put(language, (String) symptom.get(language));
                                    return filteredSymptom;
                                })
                                .collect(Collectors.toList());
                        filteredItem.put("symptoms", filteredSymptoms);
                    }

                    return filteredItem;
                })
                .collect(Collectors.toList());
    }

    // 환자용 응답 변환
    public ReportResponseDTO convertToPatientResponse(String language) {
        Map<String, Object> filteredDepartment = filterByLanguage(this.recommendedDepartment, language);
        List<Map<String, String>> filteredConditions = filterConditionsByLanguage(this.possibleConditions, language);
        List<Map<String, String>> filteredQuestions = filterQuestionsByLanguage(this.questionsForDoctor, language);
        List<Map<String, Object>> filteredChecklist = filterChecklistByLanguage(this.symptomChecklist, language);

        return ReportResponseDTO.builder()
                .reportId(this.reportId)
                .recommendedDepartment(filteredDepartment)
                .possibleConditions(filteredConditions)
                .questionsForDoctor(filteredQuestions)
                .symptomChecklist(filteredChecklist)
                .build();
    }

    // 의사용 응답 변환
    public ReportResponseDTO convertToDoctorResponse() {
        List<Map<String, String>> filteredConditions = filterConditionsByLanguage(this.possibleConditions, "KO");

        return ReportResponseDTO.builder()
                .reportId(this.reportId)
                .basicInfo(this.basicInfo)
                .healthInfo(this.healthInfo)
                .bodyInfo(this.bodyInfo)
                .symptomInfo(this.symptomInfo)
                .possibleConditions(filteredConditions)
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

