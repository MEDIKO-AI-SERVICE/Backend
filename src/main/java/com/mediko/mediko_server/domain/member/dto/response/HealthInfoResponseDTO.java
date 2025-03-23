package com.mediko.mediko_server.domain.member.dto.response;

import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
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
