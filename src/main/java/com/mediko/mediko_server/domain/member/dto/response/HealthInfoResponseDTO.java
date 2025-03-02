package com.mediko.mediko_server.domain.member.dto.response;

import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HealthInfoResponseDTO {

    private String pastHistory;

    private String familyHistory;

    private String nowMedicine;

    private String allergy;


    public static HealthInfoResponseDTO fromEntity(HealthInfo healthInfo) {
        return new HealthInfoResponseDTO(
                healthInfo.getPastHistory(),
                healthInfo.getFamilyHistory(),
                healthInfo.getNowMedicine(),
                healthInfo.getAllergy()
        );
    }

}
