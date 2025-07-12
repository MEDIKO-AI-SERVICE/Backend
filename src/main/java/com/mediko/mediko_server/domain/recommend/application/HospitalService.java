package com.mediko.mediko_server.domain.recommend.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.HealthInfoRepository;
import com.mediko.mediko_server.domain.openai.domain.DepartmentTemplate;
import com.mediko.mediko_server.domain.openai.domain.repository.DepartmentTemplateRepository;
import com.mediko.mediko_server.domain.openai.domain.unit.Intensity;
import com.mediko.mediko_server.domain.recommend.application.converter.HospitalConverter;
import com.mediko.mediko_server.domain.recommend.domain.filter.SortType;
import com.mediko.mediko_server.domain.recommend.domain.repository.HospitalRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.HospitalRequest_1DTO;
import com.mediko.mediko_server.domain.recommend.dto.request.HospitalRequest_2DTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mediko.mediko_server.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalService {
    private final BasicInfoRepository basicInfoRepository;
    private final HealthInfoRepository healthInfoRepository;
    private final DepartmentTemplateRepository departmentTemplateRepository;
    private final FlaskCommunicationService flaskCommunicationService;
    private final HospitalConverter hospitalConverter;
    private final HospitalRepository hospitalRepository;

    @Transactional
    public List<HospitalResponseDTO> recommendByDepartmentTemplate(HospitalRequest_1DTO requestDTO, Member member) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본정보가 존재하지 않습니다."));

        Optional<HealthInfo> healthInfoOpt = healthInfoRepository.findByMember(member);

        DepartmentTemplate departmentTemplate = departmentTemplateRepository.findById(requestDTO.getDepartmentTemplateId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "DepartmentTemplate을 찾을 수 없습니다."));
        String department = departmentTemplate.getDepartment();
        if (department == null) {
            throw new BadRequestException(DATA_NOT_EXIST, "DepartmentTemplate에 진료과 정보가 없습니다.");
        }
        Intensity intensity = departmentTemplate.getIntensity();

        final boolean primaryHospital;
        final boolean secondaryHospital;
        final boolean tertiaryHospital;
        switch (intensity) {
            case STRONG:
            case SEVERE:
                primaryHospital = false;
                secondaryHospital = false;
                tertiaryHospital = true;
                break;
            case UNCOMFORTABLE:
            case HARD_TO_BEAR:
                primaryHospital = false;
                secondaryHospital = true;
                tertiaryHospital = true;
                break;
            case MILD:
            case LIGHT:
                primaryHospital = true;
                secondaryHospital = true;
                tertiaryHospital = true;
                break;
            default:
                primaryHospital = false;
                secondaryHospital = false;
                tertiaryHospital = false;
        }

        final SortType sortType = SortType.RECOMMEND;

        Map<String, Object> basicInfoMap = createBasicInfoMap(basicInfo, member);
        Map<String, Object> healthInfoMap = healthInfoOpt
                .map(this::createHealthInfoMap)
                .orElse(null);

        Map<String, Object> fastApiRequest = new HashMap<>();
        fastApiRequest.put("member_id", member.getId());
        fastApiRequest.put("basic_info", basicInfoMap);
        fastApiRequest.put("health_info", healthInfoMap);
        fastApiRequest.put("department", department);
        fastApiRequest.put("lat", requestDTO.getUserLatitude());
        fastApiRequest.put("lon", requestDTO.getUserLongitude());
        fastApiRequest.put("primary_hospital", primaryHospital);
        fastApiRequest.put("secondary_hospital", secondaryHospital);
        fastApiRequest.put("tertiary_hospital", tertiaryHospital);
        fastApiRequest.put("sort_type", sortType);

        List<HospitalResponseDTO> recommendations = flaskCommunicationService.getHospitalRecommendation(fastApiRequest);
        if (recommendations == null || recommendations.isEmpty()) {
            throw new BadRequestException(DATA_NOT_EXIST, "병원 추천 정보를 받지 못했습니다.");
        }

        return recommendations.stream()
                .map(response -> hospitalConverter.toEntity(
                        response,
                        requestDTO,
                        department,
                        primaryHospital,
                        secondaryHospital,
                        tertiaryHospital,
                        member
                ))
                .map(hospitalRepository::save)
                .map(HospitalResponseDTO::fromEntity)
                .toList();
    }


    @Transactional
    public List<HospitalResponseDTO> recommendByManual(HospitalRequest_2DTO requestDTO, Member member) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본정보가 존재하지 않습니다."));

        Optional<HealthInfo> healthInfoOpt = healthInfoRepository.findByMember(member);

        if (requestDTO.getUserDepartment() == null || requestDTO.getUserDepartment().isEmpty()) {
            throw new BadRequestException(INVALID_PARAMETER, "진료과 선택은 필수입니다.");
        }

        final List<String> departmentList = requestDTO.getUserDepartment();

        Map<String, Object> basicInfoMap = createBasicInfoMap(basicInfo, member);
        Map<String, Object> healthInfoMap = healthInfoOpt
                .map(this::createHealthInfoMap)
                .orElse(null);

        Map<String, Object> fastApiRequest = new HashMap<>();
        fastApiRequest.put("member_id", member.getId());
        fastApiRequest.put("basic_info", basicInfoMap);
        fastApiRequest.put("health_info", healthInfoMap);
        fastApiRequest.put("department", departmentList);
        fastApiRequest.put("lat", requestDTO.getUserLatitude());
        fastApiRequest.put("lon", requestDTO.getUserLongitude());
        fastApiRequest.put("primary_hospital", requestDTO.isPrimaryHospital());
        fastApiRequest.put("secondary_hospital", requestDTO.isSecondaryHospital());
        fastApiRequest.put("tertiary_hospital", requestDTO.isTertiaryHospital());
        fastApiRequest.put("sort_type", requestDTO.getSortType());

        List<HospitalResponseDTO> recommendations = flaskCommunicationService.getHospitalRecommendation(fastApiRequest);
        if (recommendations == null || recommendations.isEmpty()) {
            throw new BadRequestException(DATA_NOT_EXIST, "병원 추천 정보를 받지 못했습니다.");
        }

        return recommendations.stream()
                .map(response -> hospitalConverter.toEntity(
                        response,
                        requestDTO,
                        departmentList,
                        member
                ))
                .map(hospitalRepository::save)
                .map(HospitalResponseDTO::fromEntity)
                .toList();
    }


    private Map<String, Object> createBasicInfoMap(BasicInfo basicInfo, Member member) {
        // 주소 정보 검증
        String address = member.getAddress();
        if (address == null || address.trim().isEmpty()) {
            log.error("회원 ID: {}, 주소 정보가 없습니다. address: {}", member.getId(), address);
            throw new BadRequestException(INVALID_PARAMETER, "주소 정보가 필요합니다. 기본정보에서 주소를 입력해주세요.");
        }
        
        log.info("회원 ID: {}, 주소 정보: {}", member.getId(), address);
        
        Map<String, Object> map = new HashMap<>();
        map.put("language", member.getLanguage());
        map.put("number", member.getNumber());
        map.put("address", address);
        map.put("gender", basicInfo.getGender());
        map.put("age", basicInfo.getAge());
        map.put("height", basicInfo.getHeight());
        map.put("weight", basicInfo.getWeight());
        
        log.info("Flask 서버로 보낼 basic_info 데이터: {}", map);
        return map;
    }

    private Map<String, Object> createHealthInfoMap(HealthInfo healthInfo) {
        Map<String, Object> map = new HashMap<>();
        map.put("pastHistory", healthInfo.getPastHistory());
        map.put("familyHistory", healthInfo.getFamilyHistory());
        map.put("nowMedicine", healthInfo.getNowMedicine());
        map.put("allergy", healthInfo.getAllergy());
        return map;
    }
}
