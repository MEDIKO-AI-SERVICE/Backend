package com.mediko.mediko_server.domain.recommend.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.recommend.application.converter.PharmacyConverter;
import com.mediko.mediko_server.domain.recommend.application.factory.PharmacyRequestFactory;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.domain.recommend.domain.repository.PharmacyRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.PharmacyRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_NOT_EXIST;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PharmacyService {
    private final BasicInfoRepository basicInfoRepository;
    private final PharmacyRepository pharmacyRepository;
    private final FlaskCommunicationService flaskCommunicationService;
    private final PharmacyRequestFactory pharmacyRequestFactory;
    private final PharmacyConverter pharmacyConverter;

    // 약국 추천 응답
    @Transactional
    public List<PharmacyResponseDTO> recommendPharmacy(PharmacyRequestDTO requestDTO, Member member) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본정보가 존재하지 않습니다."));

        Map<String, Object> flaskRequestData = pharmacyRequestFactory.createFlaskRequest(
                basicInfo, requestDTO.getUserLatitude(), requestDTO.getUserLongitude(), member
        );

        List<PharmacyResponseDTO> flaskResponses = flaskCommunicationService
                .getPharmacyRecommendation(flaskRequestData);

        if (flaskResponses == null || flaskResponses.isEmpty()) {
            throw new BadRequestException(DATA_NOT_EXIST, "약국 추천 정보를 받지 못했습니다.");
        }

        List<Pharmacy> savedPharmacies = flaskResponses.stream()
                .map(response -> pharmacyConverter.toEntity(response, requestDTO, basicInfo))
                .map(pharmacyRepository::save)
                .collect(Collectors.toList());

        return savedPharmacies.stream()
                .map(PharmacyResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}