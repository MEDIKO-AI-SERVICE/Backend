package com.mediko.mediko_server.domain.openai.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.application.processingState.AIProcessingState;
import com.mediko.mediko_server.domain.openai.domain.AITemplate;
import com.mediko.mediko_server.domain.openai.domain.repository.AITemplateRepository;
import com.mediko.mediko_server.domain.openai.dto.request.*;
import com.mediko.mediko_server.domain.openai.dto.response.AITemplateResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SuggestSignResponseDTO;
import com.mediko.mediko_server.domain.openai.domain.unit.Intensity;
import com.mediko.mediko_server.domain.openai.domain.unit.TimeUnit;
import com.mediko.mediko_server.global.exception.ErrorCode;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.flask.application.FastApiCommunicationService;
import com.mediko.mediko_server.global.redis.RedisUtil;
import com.mediko.mediko_server.global.s3.FilePath;
import com.mediko.mediko_server.global.s3.UuidFile;
import com.mediko.mediko_server.global.s3.UuidFileResponseDTO;
import com.mediko.mediko_server.global.s3.application.UuidFileService;
import com.mediko.mediko_server.global.s3.repository.UuidFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static com.mediko.mediko_server.global.exception.ErrorCode.INVALID_PARAMETER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AITemplateService {

    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private static final Duration STATE_DURATION = Duration.ofMinutes(30);
    private final UuidFileService uuidFileService;
    private final UuidFileRepository uuidFileRepository;
    private final AITemplateRepository aiTemplateRepository;
    private final AIReportMapper aiReportMapper;
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
            throw new BadRequestException(INVALID_PARAMETER, "상태 직렬화 오류");
        }
    }

    // JSON → 객체
    private AIProcessingState deserialize(String json) {
        try {
            return objectMapper.readValue(json, AIProcessingState.class);
        } catch (Exception e) {
            throw new BadRequestException(INVALID_PARAMETER, "상태 역직렬화 오류");
        }
    }

    // isSelf 여부 저장 -> 세션 아이디 반환
    @Transactional
    public String saveIsSelf(Member member, boolean isSelf) {
        String sessionId = UUID.randomUUID().toString();
        HealthInfo healthInfo = member.getHealthInfo();

        AIProcessingState state = AIProcessingState.builder()
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

    @Transactional
    public List<String> saveBodyPart(Member member, String sessionId, SuggestSignRequestDTO requestDTO) {
        Language language = member.getBasicInfo().getLanguage();

        AIProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);

        state = state.toBuilder()
                .bodyPart(requestDTO.getBodyPart())
                .build();
        saveState(member, sessionId, state);

        SuggestSignRequestDTO fastApiRequest = SuggestSignRequestDTO.builder()
                .language(language)
                .bodyPart(requestDTO.getBodyPart())
                .build();

        SuggestSignResponseDTO response =
                fastApiCommunicationService.postToAdjective(fastApiRequest, SuggestSignResponseDTO.class);

        return response.getAdjectives();
    }




    // adjectives에서 고른 selectedSign 저장
    @Transactional
    public void saveSelectedSign(Member member, String sessionId, List<String> selectedSigns) {
        AIProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state = state.toBuilder()
                .selectedSign(selectedSigns)
                .build();
        saveState(member, sessionId, state);
    }

    // intensity 저장
    @Transactional
    public void saveIntensity(Member member, String sessionId, Intensity intensity) {
        AIProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state = state.toBuilder().intensity(intensity).build();
        saveState(member, sessionId, state);
    }

    // startDate 저장
    @Transactional
    public void saveStartDate(Member member, String sessionId, LocalDate startDate) {
        AIProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state = state.toBuilder().startDate(startDate).build();
        saveState(member, sessionId, state);
    }

    // duration 값 저장
    @Transactional
    public void saveDurationValue(Member member, String sessionId, Integer durationValue) {
        AIProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state = state.toBuilder().durationValue(durationValue).build();
        saveState(member, sessionId, state);
    }

    // duration 단위 저장
    @Transactional
    public void saveDurationUnit(Member member, String sessionId, TimeUnit durationUnit) {
        AIProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state = state.toBuilder().durationUnit(durationUnit).build();
        saveState(member, sessionId, state);
    }

    // 추가 정보 저장
    @Transactional
    public void saveAdditional(
            Member member, String sessionId,
            boolean hasAdditional, AdditionalRequestDTO requestDTO) {
        AIProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);

        if (!hasAdditional) {
            state = state.toBuilder().additional(null).build();
        } else {
            if (requestDTO == null || requestDTO.getAdditional() == null || requestDTO.getAdditional().trim().isEmpty()) {
                throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "추가정보를 입력해야 합니다.");
            }
            state = state.toBuilder().additional(requestDTO.getAdditional()).build();
        }
        saveState(member, sessionId, state);
    }


    // 통증 이미지 저장
    @Transactional
    public AITemplateResponseDTO uploadImagesAndReturnResult(
            String sessionId, List<MultipartFile> files,
            boolean hasImages, Member member ) {
        if (hasImages) {
            if (files == null || files.isEmpty()) {
                throw new BadRequestException(INVALID_PARAMETER, "이미지를 첨부해야 합니다.");
            }
            for (MultipartFile file : files) {
                UuidFile savedFile = uuidFileService.saveFile(file, FilePath.SYMPTOM)
                        .toBuilder()
                        .sessionId(sessionId)
                        .member(member)
                        .build();
                uuidFileRepository.save(savedFile);
            }
        }
        return getResult(member, sessionId);
    }



    // 결과 호출
    public AITemplateResponseDTO getResult(Member member, String sessionId) {
        AIProcessingState state = getState(member, sessionId);
        if (state == null || !state.isComplete()) {
            throw new BadRequestException(INVALID_PARAMETER, "필수 정보가 누락되었습니다");
        }

        AITemplate aiTemplate = AITemplate.builder()
                .member(member)
                .isSelf(state.getIsSelf())
                .bodyPart(state.getBodyPart())
                .selectedSign(state.getSelectedSign())
                .sessionId(sessionId)
                .intensity(state.getIntensity())
                .startDate(state.getStartDate())
                .durationValue(state.getDurationValue())
                .durationUnit(state.getDurationUnit())
                .state(state.getState())
                .additional(state.getAdditional())
                .build();
        aiTemplateRepository.save(aiTemplate);

        List<UuidFile> files = uuidFileRepository.findAllBySessionId(sessionId);
        for (UuidFile file : files) {
            file = file.toBuilder()
                    .aiTemplate(aiTemplate)
                    .sessionId(null)
                    .build();
            uuidFileRepository.save(file);
        }

        List<UuidFile> resultFiles = uuidFileRepository.findAllByAiTemplate(aiTemplate);

        AITemplateResponseDTO fastApiResponse = callFastApiForResult(member, state);

        List<Map<String, String>> fileInfoList = resultFiles.stream()
                .map(f -> Map.of("imgUrl", f.getFileUrl()))
                .toList();

        AITemplateResponseDTO responseWithMapperFields = fastApiResponse.toBuilder()
                .basicInfo(aiReportMapper.convertToBasicInfoMap(member, state))
                .healthInfo(aiReportMapper.convertToHealthInfoMap(member, state))
                .fileInfo(fileInfoList)
                .build();

        return responseWithMapperFields;
    }


    private PatientInfoRequestDTO buildPatientInfo(AIProcessingState state) {
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

    // fastapi 요청 메서드
    private AITemplateResponseDTO callFastApiForResult(Member member, AIProcessingState state) {
        Language language = member.getBasicInfo().getLanguage();

        PatientInfoRequestDTO patientInfo = buildPatientInfo(state);

        SymptomRequest_1DTO symptom = SymptomRequest_1DTO.builder()
                .intensity(state.getIntensity())
                .startDate(state.getStartDate())
                .durationValue(state.getDurationValue())
                .durationUnit(state.getDurationUnit())
                .state(state.getState())
                .additional(state.getAdditional())
                .build();

        AITemplateRequestDTO requestDTO = AITemplateRequestDTO.builder()
                .language(language)
                .isSelf(state.getIsSelf())
                .patientInfo(patientInfo)
                .bodyPart(state.getBodyPart())
                .selectedSign(state.getSelectedSign())
                .symptom(symptom)
                .build();

        return fastApiCommunicationService.postToAiTemplate(requestDTO, AITemplateResponseDTO.class);
    }


    private void validateStateOwnership(AIProcessingState state, Member member) {
        if (state == null) {
            throw new BadRequestException(INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
    }

    public void saveState(Member member, String sessionId, AIProcessingState state) {
        String key = getStateKey(member.getId(), sessionId);
        String value = serialize(state);
        redisUtil.setValues(key, value, STATE_DURATION);
    }

    public AIProcessingState getState(Member member, String sessionId) {
        String key = getStateKey(member.getId(), sessionId);
        String json = (String) redisUtil.getValues(key);
        if (json == null) {
            return null;
        }
        return deserialize(json);
    }

    @Transactional
    public void clearState(Member member, String sessionId) {
        String key = getStateKey(member.getId(), sessionId);
        redisUtil.deleteValues(key);
    }
}
