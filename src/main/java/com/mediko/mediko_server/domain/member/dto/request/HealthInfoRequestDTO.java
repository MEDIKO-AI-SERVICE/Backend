package com.mediko.mediko_server.domain.member.dto.request;

import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HealthInfoRequestDTO {
    @Size(max = 255, message = "과거 병력 정보는 최대 255자까지 입력 가능합니다.")
    private String pastHistory;

    @Size(max = 255, message = "가족 병력 정보는 최대 255자까지 입력 가능합니다.")
    private String familyHistory;

    @Size(max = 255, message = "현재 복용 중인 약물 정보는 최대 255자까지 입력 가능합니다.")
    private String nowMedicine;

    @Size(max = 255, message = "알레르기 정보는 최대 255자까지 입력 가능합니다.")
    private String allergy;

    public HealthInfo toEntity() {
        return HealthInfo.builder()
                .pastHistory(this.pastHistory)
                .familyHistory(this.familyHistory)
                .nowMedicine(this.nowMedicine)
                .allergy(this.allergy)
                .build();
    }
}
