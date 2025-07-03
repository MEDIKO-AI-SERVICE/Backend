package com.mediko.mediko_server.domain.openai.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.Intensity;
import com.mediko.mediko_server.domain.openai.dto.request.DepartmentTemplateRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.DepartmentTemplateResposneDTO;
import com.mediko.mediko_server.global.redis.RedisUtil;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentTemplateService {

    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private static final Duration STATE_DURATION = Duration.ofMinutes(30);
    private final RestTemplate restTemplate;


    @Value("${fastapi.url.department-template}")
    private String fastApiUrl;

    // Redis Key 생성
    private String getStateKey(Long memberId, String sessionId) {
        return "DEPARTMENT_STATE:" + memberId + ":" + sessionId;
    }

    // 객체 → JSON
    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "직렬화 오류");
        }
    }

    // JSON → 객체
    private DepartmentProcessingState deserialize(String json) {
        try {
            return objectMapper.readValue(json, DepartmentProcessingState.class);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "역직렬화 오류");
        }
    }

    // 1. sign 입력 및 세션 생성
    @Transactional
    public String saveSign(Member member, String sign) {
        String sessionId = UUID.randomUUID().toString();
        DepartmentProcessingState state = DepartmentProcessingState.builder()
                .memberId(member.getId())
                .sessionId(sessionId)
                .sign(sign)
                .build();
        saveState(member, sessionId, state);
        return sessionId;
    }

    // 2. startDate 입력
    @Transactional
    public void saveStartDate(Member member, String sessionId, String startDate) {
        DepartmentProcessingState state = getState(member, sessionId);
        if (state == null) throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        state.setStartDate(startDate);
        saveState(member, sessionId, state);
    }

    // 3. intensity 입력 및 결과 반환
    @Transactional
    public DepartmentTemplateResposneDTO saveIntensityAndGetResult(Member member, String sessionId, String intensityDesc) {
        DepartmentProcessingState state = getState(member, sessionId);
        if (state == null) throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");

        Intensity intensityEnum = Intensity.fromDescription(intensityDesc);
        state.setIntensity(intensityEnum);

        saveState(member, sessionId, state);

        if (!state.isComplete()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "모든 정보를 입력해야 결과를 조회할 수 있습니다.");
        }

        return generateDepartmentResult(state);
    }


    // FastAPI department-template 호출
    private DepartmentTemplateResposneDTO generateDepartmentResult(DepartmentProcessingState state) {
        DepartmentTemplateRequestDTO requestDTO = DepartmentTemplateRequestDTO.builder()
                .sign(state.getSign())
                .startDate(LocalDate.parse(state.getStartDate()))
                .intensity(state.getIntensity())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DepartmentTemplateRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<DepartmentTemplateResposneDTO> responseEntity =
                restTemplate.exchange(fastApiUrl, HttpMethod.POST, requestEntity, DepartmentTemplateResposneDTO.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new BadRequestException(ErrorCode.INTERNAL_SERVER_ERROR, "FastAPI 서버 오류");
        }

        return responseEntity.getBody();
    }

    // 상태 저장
    private void saveState(Member member, String sessionId, DepartmentProcessingState state) {
        String key = getStateKey(member.getId(), sessionId);
        redisUtil.setValues(key, serialize(state), STATE_DURATION);
    }

    // 상태 조회
    public DepartmentProcessingState getState(Member member, String sessionId) {
        String key = getStateKey(member.getId(), sessionId);
        String json = (String) redisUtil.getValues(key);
        if (json == null) return null;
        return deserialize(json);
    }

    // 상태 삭제
    @Transactional
    public void clearState(Member member, String sessionId) {
        String key = getStateKey(member.getId(), sessionId);
        redisUtil.deleteValues(key);
    }
}
