package com.mediko.mediko_server.domain.recommend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.openai.domain.DepartmentTemplate;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HospitalRequest_1DTO {

    @JsonProperty("lat")
    private Double userLatitude;

    @JsonProperty("lon")
    private Double userLongitude;

    @JsonProperty("department_id")
    private Long departmentTemplateId;


    public Hospital toEntity(DepartmentTemplate departmentTemplate) {
        return Hospital.builder()
                .departmentTemplate(departmentTemplate)
                .userLatitude(this.userLatitude)
                .userLongitude(this.userLongitude)
                .build();
    }
}
