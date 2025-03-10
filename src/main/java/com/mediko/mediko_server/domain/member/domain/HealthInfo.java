package com.mediko.mediko_server.domain.member.domain;

import com.mediko.mediko_server.domain.member.dto.request.BasicInfoRequestDTO;
import com.mediko.mediko_server.domain.member.dto.request.HealthInfoRequestDTO;
import com.mediko.mediko_server.global.domain.BaseEntity;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.mediko.mediko_server.global.exception.ErrorCode.MISSING_REQUIRED_FIELD;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "healthInfo")
public class HealthInfo extends BaseEntity {

    @Column(name = "past_history")
    private String pastHistory;

    @Column(name = "family_history")
    private String familyHistory;

    @Column(name = "now_medicine")
    private String nowMedicine;

    @Column(name = "allergy")
    private String allergy;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    public static HealthInfo createHealthInfo(Member member) {
        HealthInfo healthInfo = HealthInfo.builder()
                .member(member)
                .build();

        member.setHealthInfo(healthInfo);

        return healthInfo;
    }

    protected void setMember(Member member) {
        this.member = member;
        if (member != null && member.getHealthInfo() != this) {
            member.setHealthInfo(this);
        }
    }

    public void validateHealthInfoFields() {
        if (this.pastHistory == null || this.pastHistory.isBlank() ||
                this.familyHistory == null || this.familyHistory.isBlank() ||
                this.nowMedicine == null || this.nowMedicine.isBlank() ||
                this.allergy == null || this.allergy.isBlank()) {
            throw new BadRequestException(MISSING_REQUIRED_FIELD, "필수 입력 항목이 누락되었습니다.");
        }
    }

    public void updateHealthInfo(HealthInfoRequestDTO healthInfoRequestDTO) {
        this.pastHistory = healthInfoRequestDTO.getPastHistory();
        this.familyHistory = healthInfoRequestDTO.getFamilyHistory();
        this.nowMedicine = healthInfoRequestDTO.getNowMedicine();
        this.allergy = healthInfoRequestDTO.getAllergy();

        validateHealthInfoFields();
    }
}
