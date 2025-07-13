package com.mediko.mediko_server.domain.member.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.MemberRepository;
import com.mediko.mediko_server.domain.member.dto.request.BasicInfoRequestDTO;
import com.mediko.mediko_server.domain.member.dto.request.LanguageRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.BasicInfoResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.ErPasswordResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.LanguageResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.SimpleErPasswordResponseDTO;
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

    private final MemberRepository memberRepository;
    private final BasicInfoRepository basicInfoRepository;
    private final FlaskCommunicationService flaskCommunicationService;



    // 사용자 기본정보 생성
    @Transactional
    public BasicInfoResponseDTO saveBasicInfo(Long memberId, BasicInfoRequestDTO requestDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 사용자입니다."));

        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseGet(() -> {
                    // BasicInfo가 없으면 새로 생성 (erPassword 자동 생성)
                    log.info("BasicInfo가 없어서 새로 생성합니다. Member ID: {}", memberId);
                    String erPassword = flaskCommunicationService.generate119Password();
                    log.info("Flask 서버에서 생성된 erPassword: {}", erPassword);
                    BasicInfo newBasicInfo = BasicInfo.createBasicInfo(member, member.getLanguage(), erPassword);
                    log.info("생성된 BasicInfo의 erPassword: {}", newBasicInfo.getErPassword());
                    // 새로 생성된 BasicInfo를 데이터베이스에 저장
                    BasicInfo savedBasicInfo = basicInfoRepository.save(newBasicInfo);
                    log.info("데이터베이스에 저장된 BasicInfo의 erPassword: {}", savedBasicInfo.getErPassword());
                    return savedBasicInfo;
                });

        if (basicInfo.getGender() != null || basicInfo.getAge() != null) {
            throw new BadRequestException(DATA_ALREADY_EXIST, "이미 기본 정보가 설정되어 있습니다.");
        }

        // 전화번호, 주소는 회원가입에서만 입력 가능. 여기서는 gender, age, height, weight, 단위만 수정
        basicInfo.setGender(requestDTO.getGender());
        basicInfo.setAge(requestDTO.getAge());
        basicInfo.setHeight(requestDTO.getHeight());
        basicInfo.setHeightUnit(requestDTO.getHeightUnit());
        basicInfo.setWeight(requestDTO.getWeight());
        basicInfo.setWeightUnit(requestDTO.getWeightUnit());

        // 수정된 BasicInfo를 데이터베이스에 저장
        BasicInfo savedBasicInfo = basicInfoRepository.save(basicInfo);
        log.info("최종 저장된 BasicInfo의 erPassword: {}", savedBasicInfo.getErPassword());

        return BasicInfoResponseDTO.fromEntity(savedBasicInfo);
    }

    // 사용자 기본정보 수정
    @Transactional
    public BasicInfoResponseDTO updateBasicInfo(Member member, BasicInfoRequestDTO requestDTO) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본 정보가 설정되지 않았습니다."));

        if (basicInfo.getGender() == null && basicInfo.getAge() == null) {
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
    public SimpleErPasswordResponseDTO getErPassword(Member member) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본 정보가 설정되지 않았습니다."));

        return SimpleErPasswordResponseDTO.fromBasicInfo(basicInfo);
    }

    // 번역된 사용자 기본정보 조회
    public BasicInfoResponseDTO getTranslatedBasicInfo(Member member) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본 정보가 설정되지 않았습니다."));

        BasicInfoResponseDTO response = BasicInfoResponseDTO.fromEntity(basicInfo);

        Language language = member.getLanguage();
        if (language != null && language != Language.KO) {
            response = flaskCommunicationService.translateBasicInfo(response, language.name().toLowerCase());
        }

        return response;
    }
}