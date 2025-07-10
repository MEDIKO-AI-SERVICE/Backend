package com.mediko.mediko_server.domain.recommend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.recommend.domain.SortType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

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


    public Hospital toEntity() {
        return Hospital.builder()
                .userLatitude(this.userLatitude)
                .userLongitude(this.userLongitude)
                .build();
    }
}
