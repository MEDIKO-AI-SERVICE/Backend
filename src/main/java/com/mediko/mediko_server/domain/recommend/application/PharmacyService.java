package com.mediko.mediko_server.domain.recommend.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.recommend.application.converter.PharmacyConverter;
import com.mediko.mediko_server.domain.recommend.application.factory.PharmacyRequestFactory;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.domain.recommend.domain.repository.PharmacyRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.PharmacyRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import com.mediko.mediko_server.global.flask.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Transactional
    public List<PharmacyResponseDTO> recommendPharmacy(PharmacyRequestDTO requestDTO) {
        // 1. BasicInfo 조회
        BasicInfo basicInfo = basicInfoRepository.findById(requestDTO.getBasicInfoId())
                .orElseThrow(() -> new RuntimeException("BasicInfo not found"));

        // 2. Flask 서버에 보낼 요청 데이터 생성
        Map<String, Object> flaskRequestData = pharmacyRequestFactory.createFlaskRequest(
                basicInfo, requestDTO.getUserLatitude(), requestDTO.getUserLongitude()
        );

        // 3. Flask 서버로 요청 보내고 응답 받기
        List<PharmacyResponseDTO> flaskResponses = flaskCommunicationService
                .getPharmacyRecommendation(flaskRequestData);

        if (flaskResponses == null || flaskResponses.isEmpty()) {
            throw new RuntimeException("No pharmacy recommendations received");
        }

        // 4. Flask 응답을 엔티티로 변환 후 저장
        List<Pharmacy> savedPharmacies = flaskResponses.stream()
                .map(response -> pharmacyConverter.toEntity(response, requestDTO, basicInfo))
                .map(pharmacyRepository::save)
                .collect(Collectors.toList());

        // 5. 저장된 엔티티를 DTO로 변환하여 반환
        return savedPharmacies.stream()
                .map(PharmacyResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}