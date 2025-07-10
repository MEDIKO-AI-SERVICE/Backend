package com.mediko.mediko_server.domain.recommend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.recommend.domain.SortType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HospitalRequest_2DTO {

    @JsonProperty("u_department")
    private List<String> userDepartment;

    @JsonProperty("sort_type")
    private SortType sortType;

    @JsonProperty("primary_hospital")
    private boolean primaryHospital;

    @JsonProperty("secondary_hospital")
    private boolean secondaryHospital;

    @JsonProperty("tertiary_hospital")
    private boolean tertiaryHospital;

    @JsonProperty("lat")
    private Double userLatitude;

    @JsonProperty("lon")
    private Double userLongitude;


    public Hospital toEntity() {
        return Hospital.builder()
                .userDepartment(this.userDepartment)
                .sortType(this.sortType)
                .primaryHospital(this.primaryHospital)
                .secondaryHospital(this.secondaryHospital)
                .tertiaryHospital(this.tertiaryHospital)
                .userLatitude(this.userLatitude)
                .userLongitude(this.userLongitude)
                .build();
    }
}

