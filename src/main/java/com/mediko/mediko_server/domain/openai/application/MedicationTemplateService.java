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
        log.info("🔑 [키생성] {}", key);
        return key;
    }

    // 객체 → JSON 문자열 변환
    private String serialize(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            log.info("📦 [직렬화] {}", json);
            return json;
        } catch (Exception e) {
            log.error("🚨 [직렬화실패] {}", e.getMessage(), e);
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "상태 직렬화 오류");
        }
    }

    // JSON 문자열 → 객체 변환
    private MedicationProcessingState deserialize(String json) {
        try {
            log.info("📦 [역직렬화시도] {}", json);
            MedicationProcessingState state = objectMapper.readValue(json, MedicationProcessingState.class);
            log.info("📦 [역직렬화성공] memberId={}", state.getMemberId());
            return state;
        } catch (Exception e) {
            log.error("🚨 [역직렬화실패] {}", e.getMessage(), e);
            log.error("🚨 [문제JSON] {}", json);
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
        log.info("✅ [세션저장완료] memberId={}, sessionId={}", member.getId(), sessionId);
        return sessionId;
    }

    // 상태 저장
    private void saveState(Member member, String sessionId, MedicationProcessingState state) {
        String key = getStateKey(member.getId(), sessionId);
        String value = serialize(state);
        redisUtil.setValues(key, value, STATE_DURATION);
        log.info("✅ [Redis저장] key={}, value={}", key, value);
    }

    // 상태 조회
    public MedicationProcessingState getState(Member member, String sessionId) {
        String key = getStateKey(member.getId(), sessionId);
        String json = (String) redisUtil.getValues(key);
        log.info("🔍 [Redis조회] key={}, value={}", key, json);
        if (json == null) {
            log.warn("⚠️ [Redis조회실패] key={}", key);
            return null;
        }
        return deserialize(json);
    }

    // 증상 설정
    @Transactional
    public void saveSign(Member member, String sessionId, String sign) {
        log.info("🔥 [saveSign시작] memberId={}, sessionId={}, sign={}", member.getId(), sessionId, sign);

        MedicationProcessingState state = getState(member, sessionId);
        log.info("🔥 [상태조회] state={}", state);

        if (state == null) {
            log.error("❌ [에러] 세션이 만료되었습니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }

        if (!state.getMemberId().equals(member.getId())) {
            log.error("❌ [에러] 세션 소유자가 아닙니다: Redis memberId={}, 현재 memberId={}", state.getMemberId(), member.getId());
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }

        log.info("🔢 [필수값검증] age={}, gender={}", state.getAge(), state.getGender());
        if (state.getAge() == null || state.getGender() == null) {
            log.error("❌ [에러] 나이와 성별을 먼저 설정해야 합니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "나이와 성별을 먼저 설정해야 합니다");
        }
        if (sign == null || sign.trim().isEmpty()) {
            log.error("❌ [에러] 증상은 필수입니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "증상은 필수입니다");
        }
        state.setSign(sign);
        saveState(member, sessionId, state);
        log.info("✅ [증상저장완료] sign={}", sign);
    }

    // 관계 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateRelation(Member member, String sessionId, String relation) {
        log.info("🔄 [관계설정] memberId={}, sessionId={}, relation={}", member.getId(), sessionId, relation);
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            log.error("❌ [에러] 세션이 만료되었습니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            log.error("❌ [에러] 세션 소유자가 아닙니다");
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setRelation(relation);
        saveState(member, sessionId, state);
        log.info("✅ [관계저장완료]");
    }

    // 성별 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateGender(Member member, String sessionId, Gender gender) {
        log.info("🔄 [성별설정] memberId={}, sessionId={}, gender={}", member.getId(), sessionId, gender);
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            log.error("❌ [에러] 세션이 만료되었습니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            log.error("❌ [에러] 세션 소유자가 아닙니다");
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setGender(gender);
        saveState(member, sessionId, state);
        log.info("✅ [성별저장완료]");
    }

    // 나이 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateAge(Member member, String sessionId, Integer age) {
        log.info("🔄 [나이설정] memberId={}, sessionId={}, age={}", member.getId(), sessionId, age);
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            log.error("❌ [에러] 세션이 만료되었습니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            log.error("❌ [에러] 세션 소유자가 아닙니다");
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setAge(age);
        saveState(member, sessionId, state);
        log.info("✅ [나이저장완료]");
    }

    // 알레르기 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateAllergy(Member member, String sessionId, String allergy) {
        log.info("🔄 [알레르기설정] memberId={}, sessionId={}, allergy={}", member.getId(), sessionId, allergy);
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            log.error("❌ [에러] 세션이 만료되었습니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            log.error("❌ [에러] 세션 소유자가 아닙니다");
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setAllergy(allergy);
        saveState(member, sessionId, state);
        log.info("✅ [알레르기저장완료]");
    }

    // 가족력 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateFamilyHistory(Member member, String sessionId, String familyHistory) {
        log.info("🔄 [가족력설정] memberId={}, sessionId={}, familyHistory={}", member.getId(), sessionId, familyHistory);
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            log.error("❌ [에러] 세션이 만료되었습니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            log.error("❌ [에러] 세션 소유자가 아닙니다");
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setFamilyHistory(familyHistory);
        saveState(member, sessionId, state);
        log.info("✅ [가족력저장완료]");
    }

    // 복용 중인 약 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updateMedication(Member member, String sessionId, String medication) {
        log.info("🔄 [복용약설정] memberId={}, sessionId={}, medication={}", member.getId(), sessionId, medication);
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            log.error("❌ [에러] 세션이 만료되었습니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            log.error("❌ [에러] 세션 소유자가 아닙니다");
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setNowMedicine(medication);
        saveState(member, sessionId, state);
        log.info("✅ [복용약저장완료]");
    }

    // 과거 병력 설정 업데이트 (소유자 검증 포함)
    @Transactional
    public void updatePastHistory(Member member, String sessionId, String pastHistory) {
        log.info("🔄 [과거병력설정] memberId={}, sessionId={}, pastHistory={}", member.getId(), sessionId, pastHistory);
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            log.error("❌ [에러] 세션이 만료되었습니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            log.error("❌ [에러] 세션 소유자가 아닙니다");
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        state.setPastHistory(pastHistory);
        saveState(member, sessionId, state);
        log.info("✅ [과거병력저장완료]");
    }

    // 결과 요청 (소유자 검증 포함)
    @Transactional
    public MedicationTemplateResponseDTO requestMedicationTemplate(Member member, String sessionId) {
        log.info("🟢 [결과요청] memberId={}, sessionId={}", member.getId(), sessionId);
        MedicationProcessingState state = getState(member, sessionId);
        if (state == null) {
            log.error("❌ [에러] 세션이 만료되었습니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            log.error("❌ [에러] 세션 소유자가 아닙니다");
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
        if (!state.isComplete()) {
            log.error("❌ [에러] 필수 정보가 누락되었습니다");
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "필수 정보가 누락되었습니다");
        }

        PatientInfoRequestDTO patientInfo = PatientInfoRequestDTO.builder()
                .language(state.getLanguage())
                .gender(state.getGender())
                .age(state.getAge())
                .allergy(state.getAllergy())
                .familyHistory(state.getFamilyHistory())
                .nowMedicine(state.getNowMedicine())
                .pastHistory(state.getPastHistory())
                .build();

        MedicationTemplateRequestDTO requestDTO = MedicationTemplateRequestDTO.builder()
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

    // 상태 삭제
    @Transactional
    public void clearState(Member member, String sessionId) {
        String key = getStateKey(member.getId(), sessionId);
        redisUtil.deleteValues(key);
        log.info("🗑️ [상태삭제] key={}", key);
    }
}
