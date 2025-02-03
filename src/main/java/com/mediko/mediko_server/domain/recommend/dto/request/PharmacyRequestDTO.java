package com.mediko.mediko_server.domain.recommend.dto.request;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Location;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PharmacyRequestDTO {

    private Long basicInfoId;

    @Nullable
    private Long locationId;

    public Pharmacy toEntity(BasicInfo basicInfo, Location location) {
        return Pharmacy.builder()
                .basicInfo(basicInfo)
                .location(location)
                .build();
    }
}
