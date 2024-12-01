package com.mediko.mediko_server.domain.member.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.UserStatus;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.MemberRepository;
import com.mediko.mediko_server.domain.member.dto.request.BasicInfoRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.BasicInfoResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
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

    //BasicInfo 저장
    @Transactional
    public String saveBasicInfo(String loginId, BasicInfoRequestDTO basicInfoRequestDTO) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 사용자입니다."));

        if (member.getBasicInfo() != null) {
            throw new BadRequestException(DATA_ALREADY_EXIST, "사용자의 기본정보가 이미 저장되었습니다.");
        }

        BasicInfo basicInfo = basicInfoRequestDTO.toEntity();
        basicInfo.validateBasicInfoFields();

        basicInfoRepository.save(basicInfo);

        member = member.toBuilder()
                .basicInfo(basicInfo)
                .build();

        member.changeRole(UserStatus.ROLE_USER);

        return memberRepository.save(member).getLoginId();
    }

    //BasicInfo 조회
    public BasicInfoResponseDTO getBasicInfo(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 사용자입니다."));

        BasicInfo basicInfo = member.getBasicInfo();
        if (basicInfo == null) {
            throw new BadRequestException(DATA_NOT_EXIST, "사용자의 기본 정보가 설정되지 않았습니다.");
        }

        return BasicInfoResponseDTO.fromEntity(basicInfo);
    }

    //BasicInfo 수정
    @Transactional
    public BasicInfoResponseDTO updateBasicInfo(String loginId, BasicInfoRequestDTO basicInfoRequestDTO) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 사용자입니다."));

        if (member.getBasicInfo() == null) {
            throw new BadRequestException(DATA_NOT_EXIST, "사용자의 기본 정보가 설정되지 않았습니다.");
        }

        BasicInfo existingBasicInfo = member.getBasicInfo();
        existingBasicInfo.updateBasicInfo(basicInfoRequestDTO);

        basicInfoRepository.save(existingBasicInfo);

        memberRepository.save(member);

        return BasicInfoResponseDTO.fromEntity(existingBasicInfo);
    }

}
