package com.mediko.mediko_server.domain.recommend.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Location;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.HealthInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.LocationRepository;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.recommend.domain.repository.HospitalRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.HospitalRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import com.mediko.mediko_server.domain.report.domain.Report;
import com.mediko.mediko_server.domain.report.domain.repository.ReportRepository;
import com.mediko.mediko_server.global.flask.FlaskCommunicationService;
import com.mediko.mediko_server.global.flask.FlaskUrls;
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
public class HospitalService {
    private final BasicInfoRepository basicInfoRepository;
    private final HealthInfoRepository healthInfoRepository;
    private final HospitalRepository hospitalRepository;
    private final ReportRepository reportRepository;
    private final LocationRepository locationRepository;
    private final FlaskCommunicationService flaskCommunicationService;


    @Transactional
    public List<HospitalResponseDTO> recommendHospital(HospitalRequestDTO requestDTO) {
        // 1. BasicInfo 및 HealthInfo 조회
        BasicInfo basicInfo = basicInfoRepository.findById(requestDTO.getBasicInfoId())
                .orElseThrow(() -> new RuntimeException("BasicInfo not found"));

        HealthInfo healthInfo = healthInfoRepository.findById(requestDTO.getHealthInfoId())
                .orElseThrow(() -> new RuntimeException("HealthInfo not found"));

        Location location = locationRepository.findById(requestDTO.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        // 2. department와 suspectedDisease 설정
        String department;
        String suspectedDisease;

        if (requestDTO.isReport()) {
            Report report = reportRepository.findById(requestDTO.getReportId())
                    .orElseThrow(() -> new RuntimeException("Report not found"));

            department = Optional.ofNullable(report.getRecommendedDepartment().get("name"))
                    .map(Object::toString)
                    .orElseThrow(() -> new RuntimeException("Department name not found"));

            suspectedDisease = report.getPossibleConditions().stream()
                    .flatMap(map -> map.values().stream())
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        } else {
            department = requestDTO.getDepartment();
            suspectedDisease = String.join(",", requestDTO.getSuspectedDisease());
        }

        // 3. Flask 서버에 보낼 요청 데이터 생성
        Map<String, Object> requestData = buildRequestData(
                basicInfo, healthInfo, location, department, suspectedDisease,
                requestDTO.isSecondary_hospital(), requestDTO.isTertiary_hospital()
        );

        // 4. Flask 서버로 요청 보내고 전체 응답 받기
        List<HospitalResponseDTO> flaskResponses = flaskCommunicationService.getHospitalRecommendation(requestData);

        if (flaskResponses == null || flaskResponses.isEmpty()) {
            throw new RuntimeException("No hospital recommendations received");
        }

        // 5. 각 추천 병원에 대해 Hospital 엔티티 생성 및 저장
        return flaskResponses.stream()
                .map(flaskResponse -> {
                    Hospital baseHospital = requestDTO.toEntity(
                            basicInfo,
                            healthInfo,
                            department,
                            suspectedDisease,
                            location
                    );
                    Hospital hospital = baseHospital.toBuilder()
                            .hpId(flaskResponse.getHpId())
                            .name(flaskResponse.getName())
                            .address(flaskResponse.getAddress())
                            .telephone(flaskResponse.getTelephone())
                            .latitude(flaskResponse.getLatitude())
                            .longitude(flaskResponse.getLongitude())
                            .esDistanceInKm(flaskResponse.getEsDistanceInKm())
                            .sidocdnm(flaskResponse.getSidocdnm())
                            .sggucdnm(flaskResponse.getSggucdnm())
                            .emdongnm(flaskResponse.getEmdongnm())
                            .clcdnm(flaskResponse.getClcdnm())
                            .url(flaskResponse.getUrl())
                            .member(basicInfo.getMember())
                            .sortScore(flaskResponse.getSortScore())
                            .departmentMatch(flaskResponse.getDepartmentMatch())
                            .latLon(flaskResponse.getLatLon())
                            .similarity(flaskResponse.getSimilarity())
                            .travelKm(flaskResponse.getTravelKm())
                            .travelH(flaskResponse.getTravelH())
                            .travelM(flaskResponse.getTravelM())
                            .travelS(flaskResponse.getTravelS())
                            .build();

                    Hospital savedHospital = hospitalRepository.save(hospital);
                    return HospitalResponseDTO.fromEntity(savedHospital);
                })
                .collect(Collectors.toList());
    }


    private Map<String, Object> buildRequestData(BasicInfo basicInfo, HealthInfo healthInfo, Location location,
                                                 String department, String suspectedDisease,
                                                 boolean secondaryHospital, boolean tertiaryHospital) {
        Map<String, Object> basicInfoMap = new HashMap<>();
        basicInfoMap.put("language", basicInfo.getLanguage().toString());
        basicInfoMap.put("number", basicInfo.getNumber());
        basicInfoMap.put("address", basicInfo.getAddress());
        basicInfoMap.put("gender", basicInfo.getGender().toString());
        basicInfoMap.put("age", basicInfo.getAge());
        basicInfoMap.put("height", basicInfo.getHeight());
        basicInfoMap.put("weight", basicInfo.getWeight());

        Map<String, Object> healthInfoMap = new HashMap<>();
        healthInfoMap.put("pastHistory", healthInfo.getPastHistory());
        healthInfoMap.put("familyHistory", healthInfo.getFamilyHistory());
        healthInfoMap.put("nowMedicine", healthInfo.getNowMedicine());
        healthInfoMap.put("allergy", healthInfo.getAllergy());

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("basic_info", basicInfoMap);
        requestMap.put("health_info", healthInfoMap);
        requestMap.put("department", department);
        requestMap.put("suspected_disease", suspectedDisease);
        requestMap.put("lat", location.getLat());
        requestMap.put("lon", location.getLon());
        requestMap.put("secondary_hospital", secondaryHospital);
        requestMap.put("tertiary_hospital", tertiaryHospital);

        return requestMap;  // Map 객체를 직접 반환
    }
}
