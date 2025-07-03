package com.mediko.mediko_server.domain.openai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentTemplateResposneDTO {

    private List<String> departmentRecommendation;

    private Map<String, String> departmentDescription;

    private List<String> questionsForDoctor;
}
