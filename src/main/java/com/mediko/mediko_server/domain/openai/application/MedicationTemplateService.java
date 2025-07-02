package com.mediko.mediko_server.domain.openai.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.openai.dto.request.MedicationTemplateRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.PatientInfoRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.MedicationTemplateResponseDTO;
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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import static com.mediko.mediko_server.global.exception.ErrorCode.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationTemplateService {
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private static final Duration STATE_DURATION = Duration.ofMinutes(30);
    private final RestTemplate restTemplate;

    @Value("${fastapi.url.medication-template}")
    private String fastApiUrl;

    // 상태 저장 키 생성
    private String getStateKey(Long memberId, String sessionId) {
        String key = "MEDICATION_STATE:" + memberId + ":" + sessionId;
        return key;
    }

    // 객체 → JSON 문자열 변환
    private String serialize(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            return json;
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "상태 직렬화 오류");
        }
    }

    // JSON 문자열 → 객체 변환
    private MedicationProcessingState deserialize(String json) {
        try {
            MedicationProcessingState state = objectMapper.readValue(json, MedicationProcessingState.class);
            return state;
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "상태 역직렬화 오류");
        }
    }

    // 본인/타인 여부 설정 + 세션 생성
    @Transactional
    public String saveIsSelf(Member member, boolean isSelf) {
        String sessionId = UUID.randomUUID().toString();
        HealthInfo healthInfo = member.getHealthInfo();

        MedicationProcessingState state = MedicationProcessingState.builder()
                .memberId(member.getId())
                .isSelf(isSelf)
                .sessionId(sessionId)
                .language(member.getBasicInfo().getLanguage())
                .build();

        if (isSelf) {
            state = state.toBuilder()
                    .age(member.getBasicInfo().getAge())
                    .gender(member.getBasicInfo().getGender())
                    .allergy(healthInfo != null ? healthInfo.getAllergy() : null)
                    .familyHistory(healthInfo != null ? healthInfo.getFamilyHistory() : null)
                    .nowMedicine(healthInfo != null ? healthInfo.getNowMedicine() : null)
                    .pastHistory(healthInfo != null ? healthInfo.getPastHistory() : null)
                    .build();
        }

        saveState(member, sessionId, state);
        return sessionId;
    }


    // 관계 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateRelation(Member member, String sessionId, String relation) {
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setRelation(relation);
        saveState(member, sessionId, state);
    }

    // 성별 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateGender(Member member, String sessionId, Gender gender) {
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setGender(gender);
        saveState(member, sessionId, state);
    }

    // 나이 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateAge(Member member, String sessionId, Integer age) {
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setAge(age);
        saveState(member, sessionId, state);
    }

    // 알레르기 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateAllergy(Member member, String sessionId, String allergy) {
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setAllergy(allergy);
        saveState(member, sessionId, state);
    }

    // 가족력 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateFamilyHistory(Member member, String sessionId, String familyHistory) {
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setFamilyHistory(familyHistory);
        saveState(member, sessionId, state);
    }

    // 복용 중인 약 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateMedication(Member member, String sessionId, String medication) {
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setNowMedicine(medication);
        saveState(member, sessionId, state);
    }

    // 과거 병력 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updatePastHistory(Member member, String sessionId, String pastHistory) {
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setPastHistory(pastHistory);
        saveState(member, sessionId, state);
    }

    @Transactional
    public MedicationTemplateResponseDTO saveSign(Member member, String sessionId, String sign) {
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        if (state.getAge() == null || state.getGender() == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "나이와 성별을 먼저 설정해야 합니다");
        }
        if (sign == null || sign.trim().isEmpty()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "증상은 필수입니다");
        }

        // 증상 저장
        state.setSign(sign);
        saveState(member, sessionId, state);

        // 결과 생성 및 반환
        return generateMedicationResult(member, sessionId, state);
    }

    private MedicationTemplateResponseDTO generateMedicationResult(
            Member member, String sessionId, MedicationProcessingState state) {

        if (!state.isComplete()) {
            log.error("❌ [에러] 필수 정보가 누락되었습니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "필수 정보가 누락되었습니다");
        }

        PatientInfoRequestDTO patientInfo = PatientInfoRequestDTO.builder()
                .gender(state.getGender())
                .age(state.getAge())
                .allergy(state.getAllergy())
                .familyHistory(state.getFamilyHistory())
                .nowMedicine(state.getNowMedicine())
                .pastHistory(state.getPastHistory())
                .build();

        MedicationTemplateRequestDTO requestDTO = MedicationTemplateRequestDTO.builder()
                .language(state.getLanguage())
                .isSelf(state.getIsSelf())
                .patientInfo(patientInfo)
                .sign(state.getSign())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MedicationTemplateRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

        log.info("🟢 [FastAPI요청] url={}, body={}", fastApiUrl, serialize(requestDTO));
        ResponseEntity<MedicationTemplateResponseDTO> responseEntity =
                restTemplate.exchange(fastApiUrl, HttpMethod.POST, requestEntity, MedicationTemplateResponseDTO.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("❌ [FastAPI오류] status={}", responseEntity.getStatusCode());
            throw new BadRequestException(INTERNAL_SERVER_ERROR, "FastAPI 서버 오류");
        }

        log.info("🟢 [FastAPI응답] body={}", responseEntity.getBody());
        return responseEntity.getBody();
    }


    // 상태 저장
    private void saveState(Member member, String sessionId, MedicationProcessingState state) {
        String key = getStateKey(member.getId(), sessionId);
        String value = serialize(state);
        redisUtil.setValues(key, value, STATE_DURATION);
    }

    // 상태 조회
    public MedicationProcessingState getState(Member member, String sessionId) {
        String key = getStateKey(member.getId(), sessionId);
        String json = (String) redisUtil.getValues(key);
        if (json == null) {
            return null;
        }
        return deserialize(json);
    }

    // 상태 삭제
    @Transactional
    public void clearState(Member member, String sessionId) {
        String key = getStateKey(member.getId(), sessionId);
        redisUtil.deleteValues(key);
    }
}
