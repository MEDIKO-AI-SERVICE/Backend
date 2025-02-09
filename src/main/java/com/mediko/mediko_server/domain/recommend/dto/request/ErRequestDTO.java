package com.mediko.mediko_server.domain.recommend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Location;
import com.mediko.mediko_server.domain.recommend.domain.Conditions;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErRequestDTO {

    private Long basicInfoId;

    @JsonProperty("lat")
    private Double userLatitude;

    @JsonProperty("lon")
    private Double userLongitude;

    private Boolean isCondition;

    private List<Long> conditions;


    public Er toEntity(BasicInfo basicInfo) {
        return Er.builder()
                .basicInfo(basicInfo)
                .userLatitude(this.userLatitude)
                .userLongitude(this.userLongitude)
                .isCondition(this.isCondition)
                .conditions(conditions)
                .build();
    }
}
