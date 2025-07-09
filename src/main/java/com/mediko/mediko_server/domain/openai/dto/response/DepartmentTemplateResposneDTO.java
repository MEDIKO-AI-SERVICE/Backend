package com.mediko.mediko_server.domain.openai.dto.response;

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

    private String department;

    private String departmentDescription;

    private List<String> questionsToDoctor;

    public static DepartmentTemplateResposneDTO fromEntity(DepartmentTemplate department) {
        return new DepartmentTemplateResposneDTO(
                department.getDepartment(),
                department.getDepartmentDescription(),
                department.getQuestionsToDoctor()
        );

    }
}
