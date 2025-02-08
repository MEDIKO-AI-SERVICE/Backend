package com.mediko.mediko_server.domain.recommend.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.HealthInfoRepository;
import com.mediko.mediko_server.domain.recommend.application.factory.HospitalRequestFactory;
import com.mediko.mediko_server.domain.recommend.application.converter.HospitalConverter;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.recommend.domain.repository.HospitalRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.HospitalRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import com.mediko.mediko_server.domain.report.domain.Report;
import com.mediko.mediko_server.domain.report.domain.repository.ReportRepository;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.exception.exceptionType.ServiceUnavailableException;
import com.mediko.mediko_server.global.flask.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mediko.mediko_server.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalService {
    private final BasicInfoRepository basicInfoRepository;
    private final HealthInfoRepository healthInfoRepository;
    private final ReportRepository reportRepository;
    private final HospitalRequestFactory hospitalRequestFactory;
    private final FlaskCommunicationService flaskCommunicationService;
    private final HospitalConverter hospitalConverter;
    private final HospitalRepository hospitalRepository;


    @Transactional
    public List<HospitalResponseDTO> recommendHospital(HospitalRequestDTO requestDTO) {
        // 1. BasicInfo 및 HealthInfo 조회
        BasicInfo basicInfo = basicInfoRepository.findById(requestDTO.getBasicInfoId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본정보가 존재하지 않습니다."));

        HealthInfo healthInfo = healthInfoRepository.findById(requestDTO.getHealthInfoId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 건강정보가 존재하지 않습니다."));

        // 2. 사용자 위치 처리 (latitude, longitude)
        if (requestDTO.getUserLatitude() == null || requestDTO.getUserLongitude() == null) {
            throw new BadRequestException(INVALID_PARAMETER, "사용자의 위도와 경도 정보는 필수입니다.");
        }
        Double userLatitude = requestDTO.getUserLatitude();
        Double userLongitude = requestDTO.getUserLongitude();

        // 3. department와 suspectedDisease 설정
        String department;
        String suspectedDisease;

        if (requestDTO.isReport()) {
            // Report 기반 처리 로직
            if (requestDTO.getReportId() == null) {
                throw new BadRequestException(INVALID_PARAMETER, "Report 정보를 사용할 경우, reportId는 필수입니다.");
            }

            if (requestDTO.getUserDepartment() != null ||
                    (requestDTO.getSuspectedDisease() != null && !requestDTO.getSuspectedDisease().isEmpty())) {
                throw new BadRequestException(INVALID_PARAMETER, "Report 정보를 사용할 경우, 진료과와 예상질병은 사용자가 입력할 수 없습니다.");
            }

            Report report = reportRepository.findById(requestDTO.getReportId())
                    .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "Report를 찾을 수 없습니다."));

            department = Optional.ofNullable(report.getRecommendedDepartment().get("korean"))
                    .map(Object::toString)
                    .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "Report에서 추천된 진료과를 찾을 수 없습니다."));

            suspectedDisease = Optional.ofNullable(report.getPossibleConditions())
                    .filter(conditions -> !conditions.isEmpty())
                    .map(conditions -> {
                        Map<String, String> firstCondition = conditions.get(0);  // String으로 받고
                        log.info("First Condition: {}", firstCondition);

                        Object nameValue = firstCondition.get("name");  // Object로 받아서
                        log.info("Name value: {}", nameValue);

                        @SuppressWarnings("unchecked")
                        List<List<String>> nameList = (List<List<String>>) nameValue;  // 캐스팅
                        return String.join(",", nameList.get(0));
                    })
                    .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "Report에서 예상 병명을 찾을 수 없습니다."));
        } else {
            // 사용자 입력 기반 처리 로직
            if (requestDTO.getUserDepartment() == null || requestDTO.getSuspectedDisease() == null ||
                    requestDTO.getSuspectedDisease().isEmpty()) {
                throw new BadRequestException(INVALID_PARAMETER, "진료과와 예상질병은 필수입니다.");
            }
            department = requestDTO.getUserDepartment();
            suspectedDisease = String.join(",", requestDTO.getSuspectedDisease());
        }

        // 4. Flask 서버에 보낼 요청 데이터 생성
        Map<String, Object> flaskRequestData = hospitalRequestFactory.createFlaskRequest(
                basicInfo, healthInfo, userLatitude, userLongitude,
                department, suspectedDisease, requestDTO.isSecondaryHospital(), requestDTO.isTertiaryHospital()
        );

        // 5. Flask 서버로 요청 보내고 응답 받기
        List<HospitalResponseDTO> flaskResponses = flaskCommunicationService
                .getHospitalRecommendation(flaskRequestData);

        if (flaskResponses == null || flaskResponses.isEmpty()) {
            throw new ServiceUnavailableException(DATA_UNAVAILABLE, "병원 추천 정보를 받지 못했습니다.");
        }

        // 6. 응답 결과를 DB에 저장
        Report report = requestDTO.isReport() ?
                reportRepository.findById(requestDTO.getReportId()).orElse(null) : null;

        for (HospitalResponseDTO response : flaskResponses) {
            Hospital hospital = hospitalConverter.toEntity(
                    response, requestDTO, department, suspectedDisease,
                    basicInfo, healthInfo, report
            );
            hospitalRepository.save(hospital);
        }

        // 7. Flask 응답을 엔티티로 변환 후 저장
        List<Hospital> savedHospitals = flaskResponses.stream()
                .map(response -> hospitalConverter.toEntity(
                        response, requestDTO, department, suspectedDisease,
                        basicInfo, healthInfo, report
                ))
                .map(hospitalRepository::save)
                .collect(Collectors.toList());

        // 8. 저장된 엔티티를 DTO로 변환하여 반환
        return savedHospitals.stream()
                .map(HospitalResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

}

