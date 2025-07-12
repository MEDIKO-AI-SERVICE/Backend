package com.mediko.mediko_server.domain.openai.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.application.processingState.AIProcessingState;
import com.mediko.mediko_server.domain.openai.application.processingState.DepartmentProcessingState;
import com.mediko.mediko_server.domain.openai.domain.AITemplate;
import com.mediko.mediko_server.domain.openai.domain.repository.AITemplateRepository;
import com.mediko.mediko_server.domain.openai.domain.unit.State;
import com.mediko.mediko_server.domain.openai.dto.request.*;
import com.mediko.mediko_server.domain.openai.dto.response.AITemplateListResponseDTO;
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

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
        return "AI_STATE:" + memberId + ":" + sessionId;
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
        AIProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);

        state = state.toBuilder()
                .bodyPart(requestDTO.getBodyPart())
                .build();
        saveState(member, sessionId, state);

        // FastAPI 요청을 위한 별도 DTO 생성 (language 포함)
        Map<String, Object> fastApiRequest = new HashMap<>();
        fastApiRequest.put("language", member.getLanguage());
        fastApiRequest.put("body_part", requestDTO.getBodyPart());

        SuggestSignResponseDTO response =
                fastApiCommunicationService.postToAdjective(fastApiRequest, SuggestSignResponseDTO.class);

        return response.getAdjectives();
    }




    // adjectives에서 고른 selectedSign 저장
    @Transactional
    public void saveSelectedSign(Member member, String sessionId, SelectedSignRequestDTO requestDTO) {
        AIProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state.setSelectedSign(requestDTO.getSelectedSign());
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


    // 상태(State) 저장
    @Transactional
    public void saveState(Member member, String sessionId, State state) {
        AIProcessingState aiState = getState(member, sessionId);
        validateStateOwnership(aiState, member);
        aiState = aiState.toBuilder().state(state).build();
        saveState(member, sessionId, aiState);
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
                UuidFile uuidFile = uuidFileService.saveFile(file, FilePath.SYMPTOM)
                        .toBuilder()
                        .sessionId(sessionId)
                        .member(member)
                        .build();
                uuidFileRepository.save(uuidFile);
            }
        }
        return getResult(member, sessionId);
    }


    // 결과 호출
    @Transactional
    public AITemplateResponseDTO getResult(Member member, String sessionId) {
        AIProcessingState state = getState(member, sessionId);
        if (state == null || !state.isComplete()) {
            throw new BadRequestException(INVALID_PARAMETER, "필수 정보가 누락되었습니다");
        }

        AITemplateResponseDTO fastApiResponse = callFastApiForResult(member, state);

        String nowKst = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

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
                .summary(fastApiResponse.getSummary())
                .department(fastApiResponse.getDepartment())
                .departmentDescription(fastApiResponse.getDepartmentDescription())
                .questionsToDoctor(fastApiResponse.getQuestionsToDoctor())
                .symptomSummary(fastApiResponse.getSymptomSummary())
                .createdAtKst(fastApiResponse.getCreatedAtKst())
                .build();
        aiTemplate = aiTemplateRepository.save(aiTemplate);

        List<UuidFile> files = uuidFileRepository.findAllBySessionId(sessionId);
        for (UuidFile file : files) {
            file.updateForResult(aiTemplate);
            uuidFileRepository.save(file);
        }

        List<UuidFile> resultFiles = uuidFileRepository.findAllByAiTemplate(aiTemplate);

        List<Map<String, String>> fileInfoList = resultFiles.stream()
                .map(f -> Map.of("imgUrl", f.getFileUrl()))
                .toList();

        return fastApiResponse.toBuilder()
                .aiTemplateId(aiTemplate.getId())
                .basicInfo(aiReportMapper.convertToBasicInfoMap(member, state))
                .healthInfo(aiReportMapper.convertToHealthInfoMap(member, state))
                .fileInfo(fileInfoList)
                .build();
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
                .isSelf(state.getIsSelf())
                .patientInfo(patientInfo)
                .bodyPart(state.getBodyPart())
                .selectedSign(state.getSelectedSign())
                .symptom(symptom)
                .build();

        // FastAPI 요청을 위한 별도 Map 생성 (language 포함)
        Map<String, Object> fastApiRequest = new HashMap<>();
        fastApiRequest.put("language", member.getLanguage());
        fastApiRequest.put("isSelf", requestDTO.isSelf());
        fastApiRequest.put("bodypart", requestDTO.getBodyPart());
        fastApiRequest.put("selectedSign", requestDTO.getSelectedSign());
        fastApiRequest.put("patientinfo", requestDTO.getPatientInfo());
        fastApiRequest.put("symptom", requestDTO.getSymptom());

        return fastApiCommunicationService.postToAiTemplate(fastApiRequest, AITemplateResponseDTO.class);
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

    // ai_id로 사전문진 결과 조회
    public Map<String, Object> getResultByAiId(Long aiId, Member member) {
        AITemplate aiTemplate = aiTemplateRepository.findById(aiId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "해당 사전문진을 찾을 수 없습니다."));

        // 본인 확인
        if (!aiTemplate.getMember().getId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "해당 사전문진에 접근할 권한이 없습니다.");
        }

        // 파일 정보 조회
        List<UuidFile> resultFiles = uuidFileRepository.findAllByAiTemplate(aiTemplate);
        List<Map<String, String>> fileInfoList = resultFiles.stream()
                .map(f -> Map.of("imgUrl", f.getFileUrl()))
                .toList();

        // AITemplateResponseDTO 생성
        AITemplateResponseDTO responseDTO = AITemplateResponseDTO.builder()
                .aiTemplateId(aiTemplate.getId())
                .createdAtKst(aiTemplate.getCreatedAtKst())
                .summary(aiTemplate.getSummary())
                .department(aiTemplate.getDepartment())
                .departmentDescription(aiTemplate.getDepartmentDescription())
                .questionsToDoctor(aiTemplate.getQuestionsToDoctor())
                .symptomSummary(aiTemplate.getSymptomSummary())
                .fileInfo(fileInfoList)
                .build();

        // AIProcessingState 재구성 (기본 정보와 건강 정보를 위해)
        AIProcessingState state = AIProcessingState.builder()
                .memberId(member.getId())
                .isSelf(aiTemplate.getIsSelf())
                .bodyPart(aiTemplate.getBodyPart())
                .selectedSign(aiTemplate.getSelectedSign())
                .intensity(aiTemplate.getIntensity())
                .startDate(aiTemplate.getStartDate())
                .durationValue(aiTemplate.getDurationValue())
                .durationUnit(aiTemplate.getDurationUnit())
                .state(aiTemplate.getState())
                .additional(aiTemplate.getAdditional())
                .build();

        // 본인인 경우 기본 정보와 건강 정보 추가
        if (aiTemplate.getIsSelf()) {
            state = state.toBuilder()
                    .age(member.getBasicInfo().getAge())
                    .gender(member.getBasicInfo().getGender())
                    .allergy(member.getHealthInfo() != null ? member.getHealthInfo().getAllergy() : null)
                    .familyHistory(member.getHealthInfo() != null ? member.getHealthInfo().getFamilyHistory() : null)
                    .nowMedicine(member.getHealthInfo() != null ? member.getHealthInfo().getNowMedicine() : null)
                    .pastHistory(member.getHealthInfo() != null ? member.getHealthInfo().getPastHistory() : null)
                    .build();
        }

        // basicInfo와 healthInfo 추가
        responseDTO = responseDTO.toBuilder()
                .basicInfo(aiReportMapper.convertToBasicInfoMap(member, state))
                .healthInfo(aiReportMapper.convertToHealthInfoMap(member, state))
                .build();

        Map<String, Object> result = new HashMap<>();
        result.put("summary", responseDTO.toSummaryMap());
        result.put("analysis", responseDTO.toAnalysisMap());

        return result;
    }

    // 사용자의 모든 사전문진 조회 (최신순)
    public List<AITemplateListResponseDTO> getAllUserResults(Member member) {
        List<AITemplate> aiTemplates = aiTemplateRepository.findByMemberOrderByCreatedAtDesc(member);
        return AITemplateListResponseDTO.fromEntityList(aiTemplates);
    }

    // 사용자의 최신 사전문진 3개 조회
    public List<AITemplateListResponseDTO> getLatestThreeResults(Member member) {
        Pageable pageable = PageRequest.of(0, 3);
        List<AITemplate> aiTemplates = aiTemplateRepository.findTop3ByMemberOrderByCreatedAtDesc(member, pageable);
        return AITemplateListResponseDTO.fromEntityList(aiTemplates);
    }
}
