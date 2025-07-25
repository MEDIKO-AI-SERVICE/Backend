package com.mediko.mediko_server.domain.openai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.openai.domain.DepartmentTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentTemplateResposneDTO {

    @JsonProperty("department_id")
    private Long departmentId;

    private String department;

    @JsonProperty("department_description")
    private String departmentDescription;

    @JsonProperty("questions_to_doctor")
    private List<String> questionsToDoctor;

    public static DepartmentTemplateResposneDTO fromEntity(DepartmentTemplate department) {
        return new DepartmentTemplateResposneDTO(
                department.getId(),
                department.getDepartment(),
                department.getDepartmentDescription(),
                department.getQuestionsToDoctor()
        );

    }
}
