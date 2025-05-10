package com.mediko.mediko_server.domain.recommend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErRequestDTO {

    @JsonProperty("lat")
    private Double userLatitude;

    @JsonProperty("lon")
    private Double userLongitude;

    private Boolean isCondition;

    private List<String> conditions;


    public Er toEntity() {
        return Er.builder()
                .userLatitude(this.userLatitude)
                .userLongitude(this.userLongitude)
                .isCondition(this.isCondition)
                .conditions(conditions)
                .build();
    }
}
