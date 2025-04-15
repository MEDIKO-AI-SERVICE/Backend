package com.mediko.mediko_server.domain.recommend.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.HealthInfoRepository;
import com.mediko.mediko_server.domain.recommend.application.factory.HospitalRequestFactory;
import com.mediko.mediko_server.domain.recommend.application.converter.HospitalConverter;
import com.mediko.mediko_server.domain.recommend.domain.DepartmentTitle;
import com.mediko.mediko_server.domain.recommend.domain.repository.HospitalRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.HospitalRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import com.mediko.mediko_server.domain.report.domain.Report;
import com.mediko.mediko_server.domain.report.domain.repository.ReportRepository;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
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

    // 병원 추천 응답
    @Transactional
    public List<HospitalResponseDTO> recommendHospital(HospitalRequestDTO requestDTO, Member member) {
        validateReportConditions(requestDTO.isReportBased(), requestDTO.getReportId());
        validateInputs(requestDTO);

        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본정보가 존재하지 않습니다."));
        HealthInfo healthInfo = healthInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 건강정보가 존재하지 않습니다."));

        final String department;
        final List<String> diseases;
        final Report report;

        if (requestDTO.isReportBased()) {
            report = reportRepository.findById(requestDTO.getReportId())
                    .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "Report를 찾을 수 없습니다."));

            department = Optional.ofNullable(report.getRecommendedDepartment())
                    .map(info -> info.get("KO"))
                    .map(Object::toString)
                    .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "Report에서 진료과 정보를 찾을 수 없습니다."));

            List<Map<String, String>> conditions = report.getPossibleConditions();
            if (conditions == null || conditions.isEmpty()) {
                throw new BadRequestException(DATA_NOT_EXIST, "Report에서 예상 질병 정보를 찾을 수 없습니다.");
            }
            diseases = conditions.stream()
                    .map(condition -> condition.get("KO").toString())
                    .collect(Collectors.toList());
        } else {
            if (requestDTO.getUserDepartment() == null) {
                throw new BadRequestException(INVALID_PARAMETER, "진료과 선택은 필수입니다.");
            }

            department = DepartmentTitle.from(requestDTO.getUserDepartment()).getValue();
            diseases = requestDTO.getSuspectedDisease();
            report = null;
        }

        Map<String, Object> flaskRequestData = hospitalRequestFactory.createFlaskRequest(
                basicInfo, healthInfo, requestDTO.getUserLatitude(), requestDTO.getUserLongitude(),
                department, diseases, requestDTO.isSecondaryHospital(), requestDTO.isTertiaryHospital(), member
        );

        List<HospitalResponseDTO> recommendations = flaskCommunicationService.getHospitalRecommendation(flaskRequestData);
        if (recommendations == null || recommendations.isEmpty()) {
            throw new BadRequestException(DATA_NOT_EXIST, "병원 추천 정보를 받지 못했습니다.");
        }

        return recommendations.stream()
                .map(response -> hospitalConverter.toEntity(
                        response, requestDTO, department, diseases, basicInfo, healthInfo, report
                ))
                .map(hospitalRepository::save)
                .map(HospitalResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateReportConditions(Boolean isReport, Long reportId) {
        if (Boolean.TRUE.equals(isReport) && reportId == null) {
            throw new BadRequestException(INVALID_PARAMETER,
                    "리포트 기반 추천 시에는 reportId를 입력해야 합니다");
        }

        if (Boolean.FALSE.equals(isReport) && reportId != null) {
            throw new BadRequestException(INVALID_PARAMETER,
                    "리포트 기반 추천이 아닌 경우 reportId를 입력할 수 없습니다");
        }
    }

    private void validateInputs(HospitalRequestDTO requestDTO) {
        if (requestDTO.isReportBased()) {
            if (requestDTO.getUserDepartment() != null || requestDTO.getSuspectedDisease() != null) {
                throw new BadRequestException(INVALID_PARAMETER,
                        "리포트 기반 추천 시에는 진료과와 예상 질병을 직접 입력할 수 없습니다.");
            }
        } else {
            if (requestDTO.getUserDepartment() == null) {
                throw new BadRequestException(INVALID_PARAMETER, "진료과 선택은 필수입니다.");
            }
        }
    }
}

