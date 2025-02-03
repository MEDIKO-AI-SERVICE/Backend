package com.mediko.mediko_server.domain.recommend.dto.request;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Location;
import com.mediko.mediko_server.domain.recommend.domain.Conditions;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ErRequestDTO {

    private Long basicInfoId;

    @Nullable
    private Long locationId;

    private Boolean isCondition;

    private List<Long> conditions;


    public Er toEntity(BasicInfo basicInfo, Location location) {

        return Er.builder()
                .basicInfo(basicInfo)
                .location(location)
                .isCondition(this.isCondition)
                .conditions(conditions)
                .build();
    }
}
