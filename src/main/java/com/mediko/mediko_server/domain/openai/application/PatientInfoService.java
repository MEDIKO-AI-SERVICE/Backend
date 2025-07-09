package com.mediko.mediko_server.domain.openai.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.openai.application.processingState.MedicationProcessingState;
import com.mediko.mediko_server.global.redis.RedisUtil;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientInfoService {

    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private static final Duration STATE_DURATION = Duration.ofMinutes(30);

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


    // 관계 설정
    @Transactional
    public void saveRelation(Member member, String sessionId, String relation) {
        MedicationProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state.setRelation(relation);
        saveState(member, sessionId, state);
    }

    // 성별 설정
    @Transactional
    public void saveOtherGender(Member member, String sessionId, Gender gender) {
        MedicationProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state.setGender(gender);
        saveState(member, sessionId, state);
    }

    // 나이 설정
    @Transactional
    public void saveOtherAge(Member member, String sessionId, Integer age) {
        MedicationProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state.setAge(age);
        saveState(member, sessionId, state);
    }

    // 알레르기 설정
    @Transactional
    public void saveOtherAllergy(Member member, String sessionId, String allergy) {
        MedicationProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state.setAllergy(allergy);
        saveState(member, sessionId, state);
    }

    // 가족력 설정
    @Transactional
    public void saveOtherFamilyHistory(Member member, String sessionId, String familyHistory) {
        MedicationProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state.setFamilyHistory(familyHistory);
        saveState(member, sessionId, state);
    }

    // 복용 중인 약 설정
    @Transactional
    public void saveOtherMedication(Member member, String sessionId, String medication) {
        MedicationProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state.setNowMedicine(medication);
        saveState(member, sessionId, state);
    }

    // 과거 병력 설정
    @Transactional
    public void saveOtherPastHistory(Member member, String sessionId, String pastHistory) {
        MedicationProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state.setPastHistory(pastHistory);
        saveState(member, sessionId, state);
    }

    // 상태 및 소유자 검증 공통 메서드
    private void validateStateOwnership(MedicationProcessingState state, Member member) {
        if (state == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
    }

    // 상태 저장
    public void saveState(Member member, String sessionId, MedicationProcessingState state) {
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
