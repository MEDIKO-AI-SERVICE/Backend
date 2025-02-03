package com.mediko.mediko_server.domain.recommend.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Location;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.LocationRepository;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import com.mediko.mediko_server.domain.recommend.domain.repository.ErRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.ErRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
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
    private final LocationRepository locationRepository;
    private final ErRepository erRepository;
    private final FlaskCommunicationService flaskCommunicationService;

    @Transactional
    public List<ErResponseDTO> recommendEr(ErRequestDTO requestDTO) {
        // 1. BasicInfo 조회
        BasicInfo basicInfo = basicInfoRepository.findById(requestDTO.getBasicInfoId())
                .orElseThrow(() -> new RuntimeException("BasicInfo not found"));

        // 2. Location 조회
        Location location = locationRepository.findById(requestDTO.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        // 3. Flask 서버에 보낼 요청 데이터 생성
        // Map으로 받아서 전달
        Map<String, Object> requestData = buildRequestData(basicInfo, location);

        // 4. Flask 서버로 요청 보내고 응답 받기
        List<ErResponseDTO> flaskResponses = flaskCommunicationService.getErRecommendation(requestData);

        // 응답 유효성 검사
        if (flaskResponses == null || flaskResponses.isEmpty()) {
            throw new RuntimeException("No hospital recommendations received");
        }

        // 5. 엔티티 변환 및 저장
        return flaskResponses.stream()
                .map(flaskResponse -> {
                    try {
                        Er baseER = requestDTO.toEntity(basicInfo, location);
                        Er er = baseER.toBuilder()
                                .name(flaskResponse.getName())
                                .address(flaskResponse.getAddress())
                                .tel(flaskResponse.getTel())
                                .hvamyn(flaskResponse.getHvamyn())
                                .isTrauma(flaskResponse.getIsTrauma())
                                .travelKm(flaskResponse.getTravelKm())
                                .travelH(flaskResponse.getTravelH())
                                .travelS(flaskResponse.getTravelS())
                                .travelM(flaskResponse.getTravelM())
                                .member(basicInfo.getMember())
                                .build();

                        Er savedER = erRepository.save(er);
                        return ErResponseDTO.fromEntity(savedER);
                    } catch (Exception e) {
                        throw new RuntimeException("병원 데이터 처리 중 오류 발생", e);
                    }
                })
                .collect(Collectors.toList());
    }

    private Map<String, Object> buildRequestData(BasicInfo basicInfo, Location location) {

        Map<String, Object> basicInfoMap = new HashMap<>();
        basicInfoMap.put("language", basicInfo.getLanguage().toString());
        basicInfoMap.put("number", basicInfo.getNumber());
        basicInfoMap.put("address", basicInfo.getAddress());
        basicInfoMap.put("gender", basicInfo.getGender().toString());
        basicInfoMap.put("age", basicInfo.getAge());
        basicInfoMap.put("height", basicInfo.getHeight());
        basicInfoMap.put("weight", basicInfo.getWeight());

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("basic_info", basicInfoMap);
        requestMap.put("lat", location.getLat());
        requestMap.put("lon", location.getLon());

        return requestMap;  // String으로 변환하지 않고 Map 그대로 반환
    }
}
