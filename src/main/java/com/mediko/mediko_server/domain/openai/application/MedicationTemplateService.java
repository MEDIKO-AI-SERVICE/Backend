package com.mediko.mediko_server.domain.openai.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.application.processingState.MedicationProcessingState;
import com.mediko.mediko_server.domain.openai.domain.MedicationTemplate;
import com.mediko.mediko_server.domain.openai.domain.repository.MedicationTemplateRepository;
import com.mediko.mediko_server.domain.openai.dto.request.MedicationTemplateRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.PatientInfoRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.MedicationTemplateResponseDTO;
import com.mediko.mediko_server.global.flask.application.FastApiCommunicationService;
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
import java.util.UUID;
import java.util.List;

import static com.mediko.mediko_server.global.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.mediko.mediko_server.global.exception.ErrorCode.INVALID_PARAMETER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationTemplateService {

    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private static final Duration STATE_DURATION = Duration.ofMinutes(30);
    private final PatientInfoService patientInfoService;
    private final MedicationTemplateRepository medicationTemplateRepository;
    private final FastApiCommunicationService fastApiCommunicationService;


    // Redis Key 생성
    private String getStateKey(Long memberId, String sessionId) {
        return "MEDICATION_STATE:" + memberId + ":" + sessionId;
    }

    // 객체 → JSON
    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "상태 직렬화 오류");
        }
    }

    // JSON → 객체
    private MedicationProcessingState deserialize(String json) {
        try {
            return objectMapper.readValue(json, MedicationProcessingState.class);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "상태 역직렬화 오류");
        }
    }

    // isSelf 여부 저장 -> 세션 아이디 반환
    @Transactional
    public String saveIsSelf(Member member, boolean isSelf) {
        String sessionId = UUID.randomUUID().toString();
        HealthInfo healthInfo = member.getHealthInfo();

        MedicationProcessingState state = MedicationProcessingState.builder()
                .memberId(member.getId())
                .isSelf(isSelf)
                .sessionId(sessionId)
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

    // sign 저장
    @Transactional
    public MedicationTemplateResponseDTO saveSign(Member member, String sessionId, String sign) {
        MedicationProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);

        if (state.getAge() == null || state.getGender() == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "나이와 성별을 먼저 설정해야 합니다");
        }

        state.setSign(sign);
        saveState(member, sessionId, state);

        if (!state.isComplete()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "필수 정보가 누락되었습니다");
        }

        MedicationTemplateResponseDTO response = callFastApiForResult(member, sessionId, state);

        MedicationTemplateRequestDTO requestDTO = MedicationTemplateRequestDTO.builder()
                .isSelf(state.getIsSelf())
                .patientInfo(PatientInfoRequestDTO.builder()
                        .gender(state.getGender())
                        .age(state.getAge())
                        .allergy(state.getAllergy())
                        .familyHistory(state.getFamilyHistory())
                        .nowMedicine(state.getNowMedicine())
                        .pastHistory(state.getPastHistory())
                        .build())
                .sign(state.getSign())
                .build();

        MedicationTemplate medication = requestDTO.toEntity(member, sessionId).toBuilder()
                .drugName(response.getDrugName())
                .drugPurpose(response.getDrugPurpose())
                .drugImageUrl(response.getDrugImageUrl())
                .wrapImageUrl(buildGoogleImageSearchUrl(response.getDrugName()))
                .pharmacistQuestion1(response.getPharmacistQuestion1())
                .pharmacistQuestion2(response.getPharmacistQuestion2())
                .pharmacistQuestion3(response.getPharmacistQuestion3())
                .build();

        medicationTemplateRepository.save(medication);

        return MedicationTemplateResponseDTO.fromEntity(medication);
    }



    // PatientInfoRequestDTO 생성 부분만 수정
    private PatientInfoRequestDTO buildPatientInfo(Member member, MedicationProcessingState state) {
        if (Boolean.TRUE.equals(state.getIsSelf()) && member.getBasicInfo() != null) {
            return PatientInfoRequestDTO.builder()
                    .height(member.getBasicInfo().getHeight())
                    .weight(member.getBasicInfo().getWeight())
                    .gender(state.getGender())
                    .age(state.getAge())
                    .allergy(state.getAllergy())
                    .familyHistory(state.getFamilyHistory())
                    .nowMedicine(state.getNowMedicine())
                    .pastHistory(state.getPastHistory())
                    .build();
        } else {
            return PatientInfoRequestDTO.builder()
                    .height(null)
                    .weight(null)
                    .gender(state.getGender())
                    .age(state.getAge())
                    .allergy(state.getAllergy())
                    .familyHistory(state.getFamilyHistory())
                    .nowMedicine(state.getNowMedicine())
                    .pastHistory(state.getPastHistory())
                    .build();
        }
    }

    // fastapi 요청 메서드
    private MedicationTemplateResponseDTO callFastApiForResult(
            Member member, String sessionId, MedicationProcessingState state) {

        if (!state.isComplete()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "필수 정보가 누락되었습니다");
        }

        Language language = member.getLanguage();
        PatientInfoRequestDTO patientInfo = buildPatientInfo(member, state);

        MedicationTemplateRequestDTO requestDTO = MedicationTemplateRequestDTO.builder()
                .language(language)
                .isSelf(state.getIsSelf())
                .patientInfo(patientInfo)
                .sign(state.getSign())
                .build();

        return fastApiCommunicationService.postToMedicationTemplate(requestDTO, MedicationTemplateResponseDTO.class);
    }

    // 약통 이미지 생성
    private String buildGoogleImageSearchUrl(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        try {
            String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
            return "https://www.google.com/search?tbm=isch&q=" + encoded;
        } catch (Exception e) {
            return null;
        }
    }

    // 세션 검증
    private void validateStateOwnership(MedicationProcessingState state, Member member) {
        if (state == null) {
            throw new BadRequestException(INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
    }

    // state 저장
    public void saveState(Member member, String sessionId, MedicationProcessingState state) {
        String key = getStateKey(member.getId(), sessionId);
        String value = serialize(state);
        redisUtil.setValues(key, value, STATE_DURATION);
    }

    // state 조회
    public MedicationProcessingState getState(Member member, String sessionId) {
        String key = getStateKey(member.getId(), sessionId);
        String json = (String) redisUtil.getValues(key);
        if (json == null) {
            return null;
        }
        return deserialize(json);
    }

    // state 삭제
    @Transactional
    public void clearState(Member member, String sessionId) {
        String key = getStateKey(member.getId(), sessionId);
        redisUtil.deleteValues(key);
    }
}
