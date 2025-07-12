package com.mediko.mediko_server.domain.recommend.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.recommend.application.converter.PharmacyConverter;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.domain.recommend.domain.filter.SortType;
import com.mediko.mediko_server.domain.recommend.domain.repository.PharmacyRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.PharmacyRequest_1DTO;
import com.mediko.mediko_server.domain.recommend.dto.request.PharmacyRequest_2DTO;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_NOT_EXIST;
import static com.mediko.mediko_server.global.exception.ErrorCode.INVALID_PARAMETER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PharmacyService {
    private final BasicInfoRepository basicInfoRepository;
    private final PharmacyRepository pharmacyRepository;
    private final FlaskCommunicationService flaskCommunicationService;
    private final PharmacyConverter pharmacyConverter;

    // PharmacyRequest_2DTO 방식 (예전 방식)
    @Transactional
    public List<PharmacyResponseDTO> recommendPharmacy(PharmacyRequest_2DTO requestDTO, Member member) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본정보가 존재하지 않습니다."));

        // 주소 정보 검증
        String address = member.getAddress();
        if (address == null || address.trim().isEmpty()) {
            log.error("회원 ID: {}, 주소 정보가 없습니다. address: {}", member.getId(), address);
            throw new BadRequestException(INVALID_PARAMETER, "주소 정보가 필요합니다. 기본정보에서 주소를 입력해주세요.");
        }
        
        log.info("회원 ID: {}, 주소 정보: {}", member.getId(), address);

        // 직접 Map 생성
        Map<String, Object> basicInfoMap = new HashMap<>();
        basicInfoMap.put("language", member.getLanguage().toString());
        basicInfoMap.put("number", member.getNumber());
        basicInfoMap.put("address", address);
        basicInfoMap.put("gender", basicInfo.getGender().toString());
        basicInfoMap.put("age", basicInfo.getAge());
        basicInfoMap.put("height", basicInfo.getHeight());
        basicInfoMap.put("weight", basicInfo.getWeight());

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("basic_info", basicInfoMap);
        requestMap.put("lat", requestDTO.getUserLatitude());
        requestMap.put("lon", requestDTO.getUserLongitude());
        requestMap.put("sort_type", requestDTO.getSortType().name());
        requestMap.put("member_id", member.getId());

        log.info("Flask 서버로 보낼 약국 추천 데이터: {}", requestMap);

        List<PharmacyResponseDTO> flaskResponses = flaskCommunicationService.getPharmacyRecommendation(requestMap);

        if (flaskResponses == null || flaskResponses.isEmpty()) {
            throw new BadRequestException(DATA_NOT_EXIST, "약국 추천 정보를 받지 못했습니다.");
        }

        List<Pharmacy> savedPharmacies = flaskResponses.stream()
                .map(response -> pharmacyConverter.toEntity(response, requestDTO, member))
                .map(pharmacyRepository::save)
                .collect(Collectors.toList());

        return savedPharmacies.stream()
                .map(PharmacyResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // PharmacyRequest_1DTO 방식 (lat/lon만 입력, sortType은 RECOMMEND로 고정)
    @Transactional
    public List<PharmacyResponseDTO> recommendPharmacy(PharmacyRequest_1DTO requestDTO, Member member) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본정보가 존재하지 않습니다."));

        // 주소 정보 검증
        String address = member.getAddress();
        if (address == null || address.trim().isEmpty()) {
            log.error("회원 ID: {}, 주소 정보가 없습니다. address: {}", member.getId(), address);
            throw new BadRequestException(INVALID_PARAMETER, "주소 정보가 필요합니다. 기본정보에서 주소를 입력해주세요.");
        }
        
        log.info("회원 ID: {}, 주소 정보: {}", member.getId(), address);

        // sortType 고정
        SortType sortType = SortType.RECOMMEND;

        // 직접 Map 생성
        Map<String, Object> basicInfoMap = new HashMap<>();
        basicInfoMap.put("language", member.getLanguage().toString());
        basicInfoMap.put("number", member.getNumber());
        basicInfoMap.put("address", address);
        basicInfoMap.put("gender", basicInfo.getGender().toString());
        basicInfoMap.put("age", basicInfo.getAge());
        basicInfoMap.put("height", basicInfo.getHeight());
        basicInfoMap.put("weight", basicInfo.getWeight());

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("basic_info", basicInfoMap);
        requestMap.put("lat", requestDTO.getUserLatitude());
        requestMap.put("lon", requestDTO.getUserLongitude());
        requestMap.put("sort_type", sortType.name());
        requestMap.put("member_id", member.getId());

        log.info("Flask 서버로 보낼 약국 추천 데이터: {}", requestMap);

        List<PharmacyResponseDTO> flaskResponses = flaskCommunicationService.getPharmacyRecommendation(requestMap);

        if (flaskResponses == null || flaskResponses.isEmpty()) {
            throw new BadRequestException(DATA_NOT_EXIST, "약국 추천 정보를 받지 못했습니다.");
        }

        List<Pharmacy> savedPharmacies = flaskResponses.stream()
                .map(response -> pharmacyConverter.toEntity(response, requestDTO, sortType, member))
                .map(pharmacyRepository::save)
                .collect(Collectors.toList());

        return savedPharmacies.stream()
                .map(PharmacyResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
