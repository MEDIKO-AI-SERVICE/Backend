package com.mediko.mediko_server.domain.recommend.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Location;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.HealthInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.LocationRepository;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.domain.recommend.domain.repository.HospitalRepository;
import com.mediko.mediko_server.domain.recommend.domain.repository.PharmacyRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.HospitalRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.request.PharmacyRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import com.mediko.mediko_server.domain.report.domain.Report;
import com.mediko.mediko_server.domain.report.domain.repository.ReportRepository;
import com.mediko.mediko_server.global.flask.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PharmacyService {
    private final BasicInfoRepository basicInfoRepository;
    private final LocationRepository locationRepository;
    private final PharmacyRepository pharmacyRepository;
    private final FlaskCommunicationService flaskCommunicationService;

    @Transactional
    public List<PharmacyResponseDTO> recommendPharmacy(PharmacyRequestDTO requestDTO) {
        // 1. BasicInfo 및 HealthInfo 조회
        BasicInfo basicInfo = basicInfoRepository.findById(requestDTO.getBasicInfoId())
                .orElseThrow(() -> new RuntimeException("BasicInfo not found"));

        Location location = locationRepository.findById(requestDTO.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));


        // 3. Flask 서버에 보낼 요청 데이터 생성
        Map<String, Object> requestData = buildRequestData(basicInfo, location);

        // 4. Flask 서버로 요청 보내고 전체 응답 받기
        List<PharmacyResponseDTO> flaskResponses = flaskCommunicationService.getPharmacyRecommendation(requestData);

        if (flaskResponses == null || flaskResponses.isEmpty()) {
            throw new RuntimeException("No hospital recommendations received");
        }

        // 5. 각 추천 병원에 대해 Hospital 엔티티 생성 및 저장
        return flaskResponses.stream()
                .map(flaskResponse -> {
                    Pharmacy basePharmacy = requestDTO.toEntity(
                            basicInfo,
                            location
                    );
                    Pharmacy pharmacy = basePharmacy.toBuilder()
                            .phId(flaskResponse.getPhId())
                            .maping(flaskResponse.getMaping())
                            .name(flaskResponse.getName())
                            .address(flaskResponse.getAddress())
                            .tel(flaskResponse.getTel())
                            .latitude(flaskResponse.getLatitude())
                            .longitude(flaskResponse.getLongitude())
                            .travelKm(flaskResponse.getTravelKm())
                            .travelH(flaskResponse.getTravelH())
                            .travelM(flaskResponse.getTravelM())
                            .travelS(flaskResponse.getTravelS())
                            .close1(flaskResponse.getClose1())
                            .close2(flaskResponse.getClose2())
                            .close3(flaskResponse.getClose3())
                            .close4(flaskResponse.getClose4())
                            .close5(flaskResponse.getClose5())
                            .close6(flaskResponse.getClose6())
                            .close7(flaskResponse.getClose7())
                            .close8(flaskResponse.getClose8())
                            .start1(flaskResponse.getStart1())
                            .start2(flaskResponse.getStart2())
                            .start3(flaskResponse.getStart3())
                            .start4(flaskResponse.getStart4())
                            .start5(flaskResponse.getStart5())
                            .start6(flaskResponse.getStart6())
                            .start7(flaskResponse.getStart7())
                            .start8(flaskResponse.getStart8())
                            .timestamp(flaskResponse.getTimestamp())
                            .version(flaskResponse.getVersion())
                            .latLon(flaskResponse.getLatLon())
                            .postcdn1(flaskResponse.getPostcdn1())
                            .postcdn2(flaskResponse.getPostcdn2())
                            .dutyetc(flaskResponse.getDutyetc())
                            .member(basicInfo.getMember())
                            .build();

                    Pharmacy savedPharmacy = pharmacyRepository.save(pharmacy);
                    return PharmacyResponseDTO.fromEntity(savedPharmacy);
                })
                .collect(Collectors.toList());
    }


    private Map<String, Object> buildRequestData(BasicInfo basicInfo,  Location location) {
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

        return requestMap;
    }
}
