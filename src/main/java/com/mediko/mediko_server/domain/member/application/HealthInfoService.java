package com.mediko.mediko_server.domain.member.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.HealthInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.MemberRepository;
import com.mediko.mediko_server.domain.member.dto.request.HealthInfoRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.HealthInfoResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
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
    private final BasicInfoRepository basicInfoRepository;
    private final FlaskCommunicationService flaskCommunicationService;

    //사용자 건강정보 저장
    @Transactional
    public HealthInfoResponseDTO saveHealthInfo(Long memberId, HealthInfoRequestDTO healthInfoRequestDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 사용자입니다."));

        if (healthInfoRepository.existsByMember(member)) {
            throw new BadRequestException(DATA_ALREADY_EXIST, "사용자의 건강정보가 이미 저장되었습니다.");
        }

        HealthInfo healthInfo = HealthInfo.createHealthInfo(member);
        healthInfo.updateHealthInfo(healthInfoRequestDTO);

        return HealthInfoResponseDTO.fromEntity(healthInfo);
    }

    //사용자 건강정보 조회
    public HealthInfoResponseDTO getHealthInfo(Member member) {
        HealthInfo healthInfo = healthInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 건강 정보가 설정되지 않았습니다."));

        return HealthInfoResponseDTO.fromEntity(healthInfo);
    }

    //사용자 건강정보 수정
    @Transactional
    public HealthInfoResponseDTO updateHealthInfo(Member member, HealthInfoRequestDTO healthInfoRequestDTO) {
        HealthInfo healthInfo = healthInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 건강 정보가 설정되지 않았습니다."));

        healthInfo.updateHealthInfo(healthInfoRequestDTO);

        return HealthInfoResponseDTO.fromEntity(healthInfo);
    }

    // 번역된 사용자 건강정보 조회
    public HealthInfoResponseDTO getTranslatedHealthInfo(Member member) {
        HealthInfo healthInfo = healthInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 건강 정보가 설정되지 않았습니다."));

        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본 정보가 설정되지 않았습니다."));

        HealthInfoResponseDTO response = HealthInfoResponseDTO.fromEntity(healthInfo);

        Language language = basicInfo.getLanguage();
        if (language != null && language != Language.KO) {
            response = flaskCommunicationService.translateHealthInfo(response, language.name().toLowerCase());
        }

        return response;
    }
}

