package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientInfoService {

    public String processRelation(Member member, String relation) {
        if (relation == null || relation.isBlank()) {
            throw new BadRequestException(ErrorCode.MISSING_REQUIRED_FIELD, "관계 정보는 필수입니다.");
        }
        return relation;
    }

    public String processGender(Member member, String gender) {
        if (gender == null || gender.isBlank()) {
            throw new BadRequestException(ErrorCode.MISSING_REQUIRED_FIELD, "성별 정보는 필수입니다.");
        }
        return gender;
    }

    public Integer processAge(Member member, Integer age) {
        if (age == null) {
            throw new BadRequestException(ErrorCode.MISSING_REQUIRED_FIELD, "나이 정보는 필수입니다.");
        }
        if (age < 0 || age > 150) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "나이는 0~150세 사이여야 합니다.");
        }
        return age;
    }

    public String processAllergy(Member member, String allergy) {
        return allergy;
    }

    public String processFamilyHistory(Member member, String familyHistory) {
        return familyHistory;
    }

    public String processNowMedicine(Member member, String nowMedicine) {
        return nowMedicine;
    }

    public String processPastHistory(Member member, String pastHistory) {
        return pastHistory;
    }
}
