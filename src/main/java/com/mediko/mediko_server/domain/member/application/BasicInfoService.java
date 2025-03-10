package com.mediko.mediko_server.domain.member.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.UserStatus;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.dto.request.BasicInfoRequestDTO;
import com.mediko.mediko_server.domain.member.dto.request.LanguageRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.BasicInfoResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.ErPasswordResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.LanguageResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mediko.mediko_server.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicInfoService {

    private final BasicInfoRepository basicInfoRepository;
    private final FlaskCommunicationService flaskCommunicationService;

    // 사용자 언어 설정
    @Transactional
    public LanguageResponseDTO setLanguage(Member member, LanguageRequestDTO languageRequestDTO) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseGet(() -> {
                    String erPassword = flaskCommunicationService.generate119Password();
                    BasicInfo newBasicInfo = BasicInfo.createBasicInfo(
                            member,
                            languageRequestDTO.getLanguage(),
                            erPassword
                    );
                    return basicInfoRepository.save(newBasicInfo);
                });

        if (basicInfo.getLanguage() != languageRequestDTO.getLanguage()) {
            basicInfo.updateLanguage(languageRequestDTO.getLanguage());
        }

        return LanguageResponseDTO.fromBasicInfo(basicInfo);
    }

    // 사용자 기본정보 생성
    @Transactional
    public BasicInfoResponseDTO saveBasicInfo(Member member, BasicInfoRequestDTO requestDTO) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "먼저 language 설정이 필요합니다."));

        if (basicInfo.getNumber() != null || basicInfo.getAddress() != null ||
                basicInfo.getGender() != null || basicInfo.getAge() != null) {
            throw new BadRequestException(DATA_ALREADY_EXIST, "이미 기본 정보가 설정되어 있습니다.");
        }

        basicInfo.updateBasicInfo(requestDTO);
        member.changeRole(UserStatus.ROLE_USER);

        return BasicInfoResponseDTO.fromEntity(basicInfo);
    }

    // 사용자 기본정보 수정
    @Transactional
    public BasicInfoResponseDTO updateBasicInfo(Member member, BasicInfoRequestDTO requestDTO) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본 정보가 설정되지 않았습니다."));

        if (basicInfo.getNumber() == null && basicInfo.getAddress() == null &&
                basicInfo.getGender() == null && basicInfo.getAge() == null) {
            throw new BadRequestException(DATA_NOT_EXIST, "먼저 기본 정보를 생성해주세요.");
        }

        basicInfo.updateBasicInfo(requestDTO);
        return BasicInfoResponseDTO.fromEntity(basicInfo);
    }

    // 사용자 기본정보 조회
    public BasicInfoResponseDTO getBasicInfo(Member member) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본 정보가 설정되지 않았습니다."));

        return BasicInfoResponseDTO.fromEntity(basicInfo);
    }

    // 119 비밀번호 조회
    public ErPasswordResponseDTO getErPassword(Member member) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본 정보가 설정되지 않았습니다."));

        return ErPasswordResponseDTO.fromBasicInfo(basicInfo);
    }
}