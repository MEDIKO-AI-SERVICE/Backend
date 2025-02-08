package com.mediko.mediko_server.domain.recommend.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Location;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.LocationRepository;
import com.mediko.mediko_server.domain.recommend.application.converter.ErConverter;
import com.mediko.mediko_server.domain.recommend.application.factory.ErRequestFactory;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import com.mediko.mediko_server.domain.recommend.domain.repository.ErRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.ErRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import com.mediko.mediko_server.global.flask.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ErService {
    private final BasicInfoRepository basicInfoRepository;
    private final ErRepository erRepository;
    private final FlaskCommunicationService flaskCommunicationService;
    private final ErRequestFactory erRequestFactory;
    private final ErConverter erConverter;

    @Transactional
    public List<ErResponseDTO> recommendEr(ErRequestDTO requestDTO) {
        // 1. BasicInfo 조회
        BasicInfo basicInfo = basicInfoRepository.findById(requestDTO.getBasicInfoId())
                .orElseThrow(() -> new RuntimeException("BasicInfo not found"));

        // 2. Flask 서버에 보낼 요청 데이터 생성
        Map<String, Object> flaskRequestData = erRequestFactory.createFlaskRequest(
                basicInfo, requestDTO.getUserLatitude(), requestDTO.getUserLongitude()
        );

        // 3. Flask 서버로 요청 보내고 응답 받기
        List<ErResponseDTO> flaskResponses = flaskCommunicationService
                .getErRecommendation(flaskRequestData);

        if (flaskResponses == null || flaskResponses.isEmpty()) {
            throw new RuntimeException("No ER recommendations received");
        }

        // 4. Flask 응답을 엔티티로 변환 후 저장
        List<Er> savedErs = flaskResponses.stream()
                .map(response -> erConverter.toEntity(response, requestDTO, basicInfo))
                .map(erRepository::save)
                .collect(Collectors.toList());

        // 5. 저장된 엔티티를 DTO로 변환하여 반환
        return savedErs.stream()
                .map(ErResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}