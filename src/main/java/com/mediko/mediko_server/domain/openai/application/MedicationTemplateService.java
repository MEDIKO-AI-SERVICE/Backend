package com.mediko.mediko_server.domain.openai.application;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationTemplateService {
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private final PatientInfoService patientInfoService;
    private static final Duration STATE_DURATION = Duration.ofMinutes(30);
    private final RestTemplate restTemplate;
    private final MedicationTemplateRepository medicationTemplateRepository;

    @Value("${fastapi.url.medication-template}")
    private String fastApiUrl;

    // 상태 저장 키 생성
    private String getStateKey(Member member) {
        return "MEDICATION_STATE:" + member.getId();
    }

    // 객체 → JSON 문자열 변환
    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "상태 직렬화 중 오류가 발생했습니다.");
        }
    }

    // JSON 문자열 → 객체 변환
    private MedicationProcessingState deserialize(String json) {
        try {
            return objectMapper.readValue(json, MedicationProcessingState.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "상태 역직렬화 중 오류가 발생했습니다.");
        }
    }

    // 상태 조회
    public MedicationProcessingState getState(Member member) {
        String key = getStateKey(member);
        String json = (String) redisUtil.getValues(key);
        return (json != null) ? deserialize(json) : new MedicationProcessingState();
    }

    // 상태 저장
    private void saveState(Member member, MedicationProcessingState state) {
        redisUtil.setValues(
                getStateKey(member),
                serialize(state),
                STATE_DURATION
        );
    }

    // 상태 초기화
    public void clearState(Member member) {
        redisUtil.deleteValues(getStateKey(member));
    }

    // Step 1: 본인/타인 여부 설정 (if-else 분기)
    public void saveIsSelf(Member member, boolean isSelf) {
        MedicationProcessingState state = getState(member);
        MedicationProcessingState.MedicationProcessingStateBuilder builder = state.toBuilder().isSelf(isSelf);

        if (isSelf) {
            // 본인 정보 자동 채움
            builder
                    .age(member.getBasicInfo().getAge())
                    .gender(member.getBasicInfo().getGender())
                    .allergy(member.getHealthInfo().getAllergy())
                    .familyHistory(member.getHealthInfo().getFamilyHistory())
                    .nowMedicine(member.getHealthInfo().getNowMedicine())
                    .pastHistory(member.getHealthInfo().getPastHistory());
        }
        builder.language(member.getBasicInfo().getLanguage());

        saveState(member, builder.build());
    }

    // Step 1-1: 관계 설정 (타인인 경우)
    public void updateRelation(Member member, String relation) {
        patientInfoService.processRelation(member, relation); // 검증
        updateField(member, builder -> builder.relation(relation));
    }

    // Step 2-1: 성별 설정
    public void updateGender(Member member, Gender gender) {
        patientInfoService.processGender(member, gender != null ? gender.name() : null); // 검증
        updateField(member, builder -> builder.gender(gender));
    }

    // Step 2-2: 나이 설정
    public void updateAge(Member member, Integer age) {
        patientInfoService.processAge(member, age); // 검증
        updateField(member, builder -> builder.age(age));
    }

    // Step 2-3: 알레르기 설정
    public void updateAllergy(Member member, String allergy) {
        patientInfoService.processAllergy(member, allergy); // 검증
        updateField(member, builder -> builder.allergy(allergy));
    }

    // Step 2-4: 가족력 설정
    public void updateFamilyHistory(Member member, String familyHistory) {
        patientInfoService.processFamilyHistory(member, familyHistory); // 검증
        updateField(member, builder -> builder.familyHistory(familyHistory));
    }

    // Step 2-5: 복용 중인 약 설정
    public void updateMedication(Member member, String nowMedicine) {
        patientInfoService.processNowMedicine(member, nowMedicine); // 검증
        updateField(member, builder -> builder.nowMedicine(nowMedicine));
    }

    // Step 2-6: 과거 병력 설정
    public void updatePastHistory(Member member, String pastHistory) {
        patientInfoService.processPastHistory(member, pastHistory); // 검증
        updateField(member, builder -> builder.pastHistory(pastHistory));
    }

    // Step 3: 증상 설정
    public void saveSign(Member member, String sign) {
        updateField(member, builder -> builder.sign(sign));
    }

    // 공통 업데이트 로직
    private void updateField(Member member,
                             java.util.function.Function<MedicationProcessingState.MedicationProcessingStateBuilder,
                                     MedicationProcessingState.MedicationProcessingStateBuilder> updater) {
        MedicationProcessingState state = getState(member);
        MedicationProcessingState.MedicationProcessingStateBuilder builder = state.toBuilder();
        builder = updater.apply(builder);
        saveState(member, builder.build());
    }

    /**
     * FastAPI로 MedicationTemplateRequestDTO를 전송하고 응답을 받는 메서드
     */
    @Transactional
    public MedicationTemplateResponseDTO requestMedicationTemplateToFastApi(Member member) {
        // 1. Redis에서 상태 가져오기
        MedicationProcessingState state = getState(member);

        // 2. 필수값 검증
        if (!state.isComplete()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "필수 정보가 누락되었습니다. (증상, 나이, 성별)");
        }

        // 3. Mapper로 변환
        MedicationTemplateRequestDTO requestDTO = MedicationRequestMapper.toDrugTemplateRequestDTO(state);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MedicationTemplateRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        try {
            // 4. FastAPI로부터 응답 받기
            ResponseEntity<MedicationTemplateResponseDTO> response = restTemplate.exchange(
                    fastApiUrl,
                    HttpMethod.POST,
                    entity,
                    MedicationTemplateResponseDTO.class
            );
            MedicationTemplateResponseDTO fastApiResult = response.getBody();

            if (fastApiResult == null) {
                throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "FastAPI 서버로부터 응답을 받지 못했습니다.");
            }

            // 5. medicationImageUrls 생성
            Map<String, String> medicationImageUrls_2 = makeMedicationImageUrls(fastApiResult.getMedicationNames());

            // 6. 새로운 DTO에 모든 값 합치기
            MedicationTemplateResponseDTO result = new MedicationTemplateResponseDTO(
                    fastApiResult.getMedicationNames(),
                    fastApiResult.getMedicationIndications(),
                    fastApiResult.getMedicationImageUrls_1(),
                    medicationImageUrls_2,
                    fastApiResult.getQuestionsForPharmacist(),
                    fastApiResult.getWarningMessage()
            );

            // 7. DB 저장 로직
            saveMedicationTemplate(member, state, result);
            clearState(member);

            return result;
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INTERNAL_SERVER_ERROR, "FastAPI 서버 통신 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public void saveMedicationTemplate(Member member, MedicationProcessingState state, MedicationTemplateResponseDTO response) {
        try {
            // MedicationTemplate 엔티티 생성 (PatientInfo는 저장하지 않음)
            MedicationTemplate template = MedicationTemplate.builder()
                    .member(member)
                    .isSelf(state.getIsSelf())
                    .sign(state.getSign())
                    // FastAPI 응답을 JSON으로 저장
                    .medicationNames(objectMapper.writeValueAsString(response.getMedicationNames()))
                    .medicationIndications(objectMapper.writeValueAsString(response.getMedicationIndications()))
                    .medicationImageUrls_1(objectMapper.writeValueAsString(response.getMedicationImageUrls_1()))
                    .medicationImageUrls_2(objectMapper.writeValueAsString(response.getMedicationImageUrls_2()))
                    .questionsForPharmacist(objectMapper.writeValueAsString(response.getQuestionsForPharmacist()))
                    .warningMessage(response.getWarningMessage())
                    .build();

            // MedicationTemplate만 저장
            medicationTemplateRepository.save(template);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new BadRequestException(ErrorCode.INTERNAL_SERVER_ERROR, "응답 데이터 저장 중 오류가 발생했습니다.");
        }
    }

    private Map<String, String> makeMedicationImageUrls(List<String> medicationNames) {
        return medicationNames.stream()
                .collect(Collectors.toMap(
                        name -> name,
                        name -> "https://www.google.com/search?tbm=isch&q=" + URLEncoder.encode(name, StandardCharsets.UTF_8)
                ));
    }
}