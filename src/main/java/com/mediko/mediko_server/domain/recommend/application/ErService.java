package com.mediko.mediko_server.domain.recommend.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.recommend.application.converter.ErConverter;
import com.mediko.mediko_server.domain.recommend.application.factory.ErRequestFactory;
import com.mediko.mediko_server.domain.recommend.domain.filter.Condition;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import com.mediko.mediko_server.domain.recommend.domain.repository.ErRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.ErRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_NOT_EXIST;
import static com.mediko.mediko_server.global.exception.ErrorCode.INVALID_PARAMETER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ErService {
    private final BasicInfoRepository basicInfoRepository;
    private final ErRepository erRepository;
    private final FlaskCommunicationService flaskCommunicationService;
    private final ErRequestFactory erRequestFactory;
    private final ErConverter erConverter;


    // 응급실 추천 응답
    @Transactional
    public List<ErResponseDTO> recommendEr(ErRequestDTO requestDTO, Member member) {
        validateConditions(requestDTO.getIsCondition(), requestDTO.getConditions());
        validateCoordinates(requestDTO.getUserLatitude(), requestDTO.getUserLongitude());

        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본정보가 존재하지 않습니다."));

        Map<String, Object> flaskRequestData = erRequestFactory.createFlaskRequest(
                basicInfo, requestDTO.getUserLatitude(), requestDTO.getUserLongitude(), member
        );

        List<ErResponseDTO> flaskResponses = flaskCommunicationService
                .getErRecommendation(flaskRequestData);

        if (flaskResponses == null || flaskResponses.isEmpty()) {
            throw new BadRequestException(DATA_NOT_EXIST, "응급실 추천 정보 받지 못했습니다.");
        }

        List<Er> savedErs = flaskResponses.stream()
                .map(response -> erConverter.toEntity(response, requestDTO, member))
                .map(erRepository::save)
                .collect(Collectors.toList());

        return savedErs.stream()
                .map(ErResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }


    private void validateConditions(Boolean isCondition, List<String> conditions) {
        if (Boolean.FALSE.equals(isCondition) && conditions != null && !conditions.isEmpty()) {
            throw new BadRequestException(INVALID_PARAMETER, "특수 상태를 입력할 수 없습니다.");
        }

        if (Boolean.TRUE.equals(isCondition) && (conditions == null || conditions.isEmpty())) {
            throw new BadRequestException(INVALID_PARAMETER, "특수 상태를 입력해야 합니다.");
        }

        if (conditions != null && !conditions.isEmpty()) {
            Set<String> validConditions = Arrays.stream(Condition.values())
                    .map(Condition::getDescription)
                    .collect(Collectors.toSet());

            for (String condition : conditions) {
                if (!validConditions.contains(condition)) {
                    throw new BadRequestException(INVALID_PARAMETER, "유효하지 않은 특수상태입니다.");
                }
            }
        }
    }

    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null && longitude == null) {
            throw new BadRequestException(INVALID_PARAMETER, "위경도 정보가 필요합니다.");
        }
    }
}