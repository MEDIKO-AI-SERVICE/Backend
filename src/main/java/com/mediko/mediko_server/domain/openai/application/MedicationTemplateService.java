package com.mediko.mediko_server.domain.openai.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.openai.domain.MedicationTemplate;
import com.mediko.mediko_server.domain.openai.domain.repository.MedicationTemplateRepository;
import com.mediko.mediko_server.domain.openai.dto.request.MedicationTemplateRequestDTO;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationTemplateService {
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private static final Duration STATE_DURATION = Duration.ofMinutes(30);
    private final RestTemplate restTemplate;
    private final MedicationTemplateRepository medicationTemplateRepository;

    @Value("${fastapi.url.medication-template}")
    private String fastApiUrl;

    // 상태 저장 키 생성
    private String getStateKey(Long memberId, String sessionId) {
        return "MEDICATION_STATE:" + memberId + ":" + sessionId;
    }

    // 객체 → JSON 문자열 변환
    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "상태 직렬화 오류");
        }
    }

    // JSON 문자열 → 객체 변환
    private MedicationProcessingState deserialize(String json) {
        try {
            return objectMapper.readValue(json, MedicationProcessingState.class);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "상태 역직렬화 오류");
        }
    }

    // 본인/타인 여부 설정 + 세션 생성
    public String saveIsSelf(Member member, boolean isSelf) {
        String sessionId = UUID.randomUUID().toString();

        MedicationProcessingState state = MedicationProcessingState.builder()
                .isSelf(isSelf)
                .sessionId(sessionId)
                .language(member.getBasicInfo().getLanguage())
                .build();

        if (isSelf) {
            HealthInfo healthInfo = member.getHealthInfo();
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

    // 상태 저장
    private void saveState(Member member, String sessionId, MedicationProcessingState state) {
        redisUtil.setValues(
                getStateKey(member.getId(), sessionId),
                serialize(state),
                STATE_DURATION
        );
    }

    // 상태 조회
    public MedicationProcessingState getState(Member member, String sessionId) {
        String key = getStateKey(member.getId(), sessionId);
        String json = (String) redisUtil.getValues(key);
        return (json != null) ? deserialize(json) : null;
    }

    // 관계 설정
    public void updateRelation(Member member, String sessionId, String relation) {
        updateField(member, sessionId, builder -> builder.relation(relation));
    }

    // 성별 설정
    public void updateGender(Member member, String sessionId, Gender gender) {
        updateField(member, sessionId, builder -> builder.gender(gender));
    }

    // 나이 설정
    public void updateAge(Member member, String sessionId, Integer age) {
        updateField(member, sessionId, builder -> builder.age(age));
    }

    // 알레르기 설정
    public void updateAllergy(Member member, String sessionId, String allergy) {
        updateField(member, sessionId, builder -> builder.allergy(allergy));
    }

    // 가족력 설정
    public void updateFamilyHistory(Member member, String sessionId, String familyHistory) {
        updateField(member, sessionId, builder -> builder.familyHistory(familyHistory));
    }

    // 복용 중인 약 설정
    public void updateMedication(Member member, String sessionId, String nowMedicine) {
        updateField(member, sessionId, builder -> builder.nowMedicine(nowMedicine));
    }

    // 과거 병력 설정
    public void updatePastHistory(Member member, String sessionId, String pastHistory) {
        updateField(member, sessionId, builder -> builder.pastHistory(pastHistory));
    }

    // 증상 설정
    public void saveSign(Member member, String sessionId, String sign) {
        log.info("🔥 [saveSign 시작] memberId={}, sessionId={}", member.getId(), sessionId);

        // 1. Redis에서 상태 조회
        MedicationProcessingState state = getState(member, sessionId);
        log.info("🔥 [조회된 상태] state={}", state); // NULL 여부 확인

        // 2. null 체크
        if (state == null) {
            log.error("🔥 [에러] 세션이 만료되었거나 존재하지 않음");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }

        // 3. age/gender null 체크
        if (state.getAge() == null || state.getGender() == null) {
            log.error("🔥 [에러] age={}, gender={}", state.getAge(), state.getGender());
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "나이와 성별을 먼저 설정해야 합니다");
        }

        // 4. sign 유효성 체크
        if (sign == null || sign.trim().isEmpty()) {
            log.error("🔥 [에러] sign={}", sign);
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "증상은 필수입니다");
        }

        // 5. 상태 업데이트
        state.setSign(sign);
        saveState(member, sessionId, state);
        log.info("🔥 [저장 완료] sign={}", sign);
    }


    // 공통 업데이트 메서드
    private void updateField(Member member, String sessionId,
                             java.util.function.Function<MedicationProcessingState.MedicationProcessingStateBuilder,
                                     MedicationProcessingState.MedicationProcessingStateBuilder> updater) {
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }

        MedicationProcessingState.MedicationProcessingStateBuilder builder = state.toBuilder();
        builder = updater.apply(builder);
        saveState(member, sessionId, builder.build());
    }

    // 처방전 생성 요청
    @Transactional
    public MedicationTemplateResponseDTO requestMedicationTemplate(Member member, String sessionId) {
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }

        if (!state.isComplete()) {
            StringBuilder missing = new StringBuilder();
            if (state.getSign() == null) missing.append("증상, ");
            if (state.getAge() == null) missing.append("나이, ");
            if (state.getGender() == null) missing.append("성별, ");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER,
                    "필수 정보 누락: " + missing.substring(0, missing.length() - 2));
        }

        MedicationTemplateRequestDTO requestDTO = MedicationRequestMapper.toDrugTemplateRequestDTO(state);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MedicationTemplateRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        try {
            ResponseEntity<MedicationTemplateResponseDTO> response = restTemplate.exchange(
                    fastApiUrl, HttpMethod.POST, entity, MedicationTemplateResponseDTO.class
            );

            MedicationTemplateResponseDTO result = response.getBody();
            if (result == null) throw new BadRequestException(ErrorCode.INTERNAL_SERVER_ERROR, "FastAPI 응답 오류");

            saveMedicationTemplate(member, sessionId, state, result);
            clearState(member, sessionId);

            return result;
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INTERNAL_SERVER_ERROR, "FastAPI 통신 오류");
        }
    }

    // 상태 삭제
    public void clearState(Member member, String sessionId) {
        redisUtil.deleteValues(getStateKey(member.getId(), sessionId));
    }

    // DB에 처방전 저장
    @Transactional
    public void saveMedicationTemplate(Member member, String sessionId,
                                       MedicationProcessingState state,
                                       MedicationTemplateResponseDTO response) {
        try {
            MedicationTemplate template = MedicationTemplate.builder()
                    .member(member)
                    .sessionId(sessionId)
                    .isSelf(state.getIsSelf())
                    .sign(state.getSign())
                    .medicationNames(objectMapper.writeValueAsString(response.getMedicationNames()))
                    .medicationIndications(objectMapper.writeValueAsString(response.getMedicationIndications()))
                    .medicationImageUrls_1(objectMapper.writeValueAsString(response.getMedicationImageUrls_1()))
                    .medicationImageUrls_2(objectMapper.writeValueAsString(response.getMedicationImageUrls_2()))
                    .questionsForPharmacist(objectMapper.writeValueAsString(response.getQuestionsForPharmacist()))
                    .warningMessage(response.getWarningMessage())
                    .build();

            medicationTemplateRepository.save(template);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INTERNAL_SERVER_ERROR, "응답 데이터 저장 오류");
        }
    }
}
