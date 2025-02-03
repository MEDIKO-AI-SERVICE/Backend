package com.mediko.mediko_server.domain.recommend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Location;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HospitalRequestDTO {

    private Long basicInfoId;

    private Long healthInfoId;

    @Nullable
    private Long locationId;

    @JsonProperty("is_report")
    private boolean isReport;

    private Long reportId;

    private String department;

    private List<String> suspectedDisease;

    private boolean secondary_hospital;

    private boolean tertiary_hospital;

    public Hospital toEntity(BasicInfo basicInfo, HealthInfo healthInfo, String department, String suspectedDisease, Location location) {
        return Hospital.builder()
                .isReport(this.isReport)
                .department(department)
                .suspectedDisease(List.of(suspectedDisease.split(",")))
                .secondaryHospital(this.secondary_hospital)
                .tertiaryHospital(this.tertiary_hospital)
                .basicInfo(basicInfo)
                .healthInfo(healthInfo)
                .location(location)
                .build();
    }
}

