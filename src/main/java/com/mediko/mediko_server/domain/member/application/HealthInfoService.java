package com.mediko.mediko_server.domain.member.application;

import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.repository.HealthInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.MemberRepository;
import com.mediko.mediko_server.domain.member.dto.request.HealthInfoRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.BasicInfoResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.HealthInfoResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_ALREADY_EXIST;
import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_NOT_EXIST;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthInfoService {

    private final MemberRepository memberRepository;
    private final HealthInfoRepository healthInfoRepository;

    //HealthInfo 저장
    public String saveHealthInfo(String loginId, HealthInfoRequestDTO healthInfoRequestDTO) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 사용자입니다."));

        if (member.getHealthInfo() != null) {
            throw new BadRequestException(DATA_ALREADY_EXIST, "사용자의 기본정보가 이미 저장되었습니다.");
        }

        HealthInfo healthInfo = healthInfoRequestDTO.toEntity();
        healthInfo.validateHealthInfoFields();

        healthInfoRepository.save(healthInfo);

        member = member.toBuilder()
                .healthInfo(healthInfo)
                .build();

        return memberRepository.save(member).getLoginId();
    }


    //HealthInfo 조회
    public HealthInfoResponseDTO getHealthInfo(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 사용자입니다."));

        HealthInfo healthInfo = member.getHealthInfo();
        if (healthInfo == null) {
            throw new BadRequestException(DATA_NOT_EXIST, "사용자의 건강 정보가 설정되지 않았습니다.");
        }

        return HealthInfoResponseDTO.fromEntity(healthInfo);
    }


    //HealthInfo 수정
    public HealthInfoResponseDTO updateHealthInfo(String loginId, HealthInfoRequestDTO healthInfoRequestDTO) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 사용자입니다."));

        if (member.getHealthInfo() == null) {
            throw new BadRequestException(DATA_NOT_EXIST, "사용자의 건강 정보가 설정되지 않았습니다.");
        }

        HealthInfo existingHealthInfo = member.getHealthInfo();
        existingHealthInfo.updateHealthInfo(healthInfoRequestDTO);

        healthInfoRepository.save(existingHealthInfo);

        memberRepository.save(member);

        return HealthInfoResponseDTO.fromEntity(existingHealthInfo);
    }
}

