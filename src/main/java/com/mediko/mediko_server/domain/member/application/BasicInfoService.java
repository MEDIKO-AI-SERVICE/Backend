package com.mediko.mediko_server.domain.member.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.UserStatus;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.MemberRepository;
import com.mediko.mediko_server.domain.member.dto.request.BasicInfoRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.BasicInfoResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.flask.FlaskCommunicationService;
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

    // 사용자 기본정보 저장
    @Transactional
    public BasicInfoResponseDTO saveBasicInfo(Member member, BasicInfoRequestDTO basicInfoRequestDTO) {

        if (basicInfoRepository.existsByMember(member)) {
            throw new BadRequestException(DATA_ALREADY_EXIST, "사용자의 기본정보가 이미 저장되었습니다.");
        }

        String erPassword = flaskCommunicationService.generate119Password();

        BasicInfo basicInfo = basicInfoRequestDTO.toEntity()
                .toBuilder()
                .erPassword(erPassword)
                .build();

        member.setBasicInfo(basicInfo);

        basicInfo.validateBasicInfoFields();
        BasicInfo savedBasicInfo = basicInfoRepository.save(basicInfo);

        member.changeRole(UserStatus.ROLE_USER);
        memberRepository.save(member);

        return BasicInfoResponseDTO.fromEntity(savedBasicInfo);
    }

    // 사용자 기본정보 조회
    public BasicInfoResponseDTO getBasicInfo(Member member) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본 정보가 설정되지 않았습니다."));

        return BasicInfoResponseDTO.fromEntity(basicInfo);
    }

    // 사용자 기본정보 수정
    @Transactional
    public BasicInfoResponseDTO updateBasicInfo(Member member, BasicInfoRequestDTO basicInfoRequestDTO) {
        BasicInfo existingBasicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본 정보가 설정되지 않았습니다."));

        existingBasicInfo.updateBasicInfo(basicInfoRequestDTO);
        basicInfoRepository.save(existingBasicInfo);

        return BasicInfoResponseDTO.fromEntity(existingBasicInfo);
    }
}
