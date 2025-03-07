package com.mediko.mediko_server.domain.recommend.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.recommend.application.converter.ErConverter;
import com.mediko.mediko_server.domain.recommend.application.factory.ErRequestFactory;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import com.mediko.mediko_server.domain.recommend.domain.repository.ErRepository;
import com.mediko.mediko_server.domain.recommend.dto.request.ErRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.GeocodeResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_NOT_EXIST;

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
    private final GeocodeService geocodeService;

    @Transactional
    public List<ErResponseDTO> recommendEr(ErRequestDTO requestDTO, Member member) {
        // 1. BasicInfo 조회
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "사용자의 기본정보가 존재하지 않습니다."));

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
        List<ErResponseDTO> responsesWithCoordinates = flaskResponses.stream()
                .map(response -> {
                    try {
                        GeocodeResponseDTO coords = geocodeService.getAddressToCoords(response.getAddress());
                        return new ErResponseDTO(
                                response.getId(),
                                response.getName(),
                                response.getAddress(),
                                response.getTel(),
                                response.getHvamyn(),
                                response.getIsTrauma(),
                                response.getTravelKm(),
                                response.getTravelH(),
                                response.getTravelM(),
                                response.getTravelS(),
                                coords.getLatitude(),  // 변환된 위도
                                coords.getLongitude()  //환된 경도
                        );
                    } catch (Exception e) {
                        log.error("Error converting address to coordinates for: " + response.getAddress(), e);
                        return response; // 변환 실패 시 원본 데이터 유지
                    }
                })
                .collect(Collectors.toList());

        // 변환된 데이터로 저장 및 반환
        List<Er> savedErs = responsesWithCoordinates.stream()
                .map(response -> erConverter.toEntity(response, requestDTO, basicInfo))
                .map(erRepository::save)
                .collect(Collectors.toList());

        return savedErs.stream()
                .map(ErResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}