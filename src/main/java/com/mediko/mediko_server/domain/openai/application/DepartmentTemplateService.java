package com.mediko.mediko_server.domain.openai.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.application.processingState.AIProcessingState;
import com.mediko.mediko_server.domain.openai.application.processingState.DepartmentProcessingState;
import com.mediko.mediko_server.domain.openai.domain.DepartmentTemplate;
import com.mediko.mediko_server.domain.openai.domain.repository.DepartmentTemplateRepository;
import com.mediko.mediko_server.domain.openai.domain.unit.Intensity;
import com.mediko.mediko_server.domain.openai.dto.request.*;
import com.mediko.mediko_server.domain.openai.dto.response.DepartmentTemplateResposneDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SuggestSignResponseDTO;
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

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mediko.mediko_server.global.exception.ErrorCode.INVALID_PARAMETER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentTemplateService {

    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private static final Duration STATE_DURATION = Duration.ofMinutes(30);
    private final DepartmentTemplateRepository departmentTemplateRepository;
    private final FastApiCommunicationService fastApiCommunicationService;


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


    // bodypart 저장 및 adjectives, 세션 아이디 반환
    @Transactional
    public Map<String, Object> saveBodyPart(Member member, SuggestSignRequestDTO requestDTO) {
        String sessionId = UUID.randomUUID().toString();

        DepartmentProcessingState state = DepartmentProcessingState.builder()
                .memberId(member.getId())
                .sessionId(sessionId)
                .bodyPart(requestDTO.getBodyPart())
                .build();
        saveState(member, sessionId, state);

        Language language = member.getBasicInfo().getLanguage();
        SuggestSignRequestDTO fastApiRequest = SuggestSignRequestDTO.builder()
                .language(language)
                .bodyPart(requestDTO.getBodyPart())
                .build();

        SuggestSignResponseDTO response =
                fastApiCommunicationService.postToAdjective(fastApiRequest, SuggestSignResponseDTO.class);

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("adjectives", response.getAdjectives());
        return result;
    }



    // adjectives에서 고른 selectedSign 저장
    @Transactional
    public void saveSelectedSign(Member member, String sessionId, SelectedSignRequestDTO requestDTO) {
        DepartmentProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state.setSelectedSign(requestDTO.getSelectedSign());
        saveState(member, sessionId, state);
    }

    // startDate 저장
    @Transactional
    public void saveStartDate(Member member, String sessionId, String startDate) {
        DepartmentProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state.setStartDate(startDate);
        saveState(member, sessionId, state);
    }

    // intensity 저장
    @Transactional
    public void saveIntensity(Member member, String sessionId, String intensityDesc) {
        DepartmentProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        Intensity intensityEnum = Intensity.fromDescription(intensityDesc);
        state.setIntensity(intensityEnum);
        saveState(member, sessionId, state);
    }

    // 추가 정보 저장
    @Transactional
    public void saveAdditional(Member member, String sessionId, AdditionalRequestDTO requestDTO) {
        DepartmentProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);
        state.setAdditional(requestDTO.getAdditional());
        saveState(member, sessionId, state);
    }

    // 결과 조회
    @Transactional
    public DepartmentTemplateResposneDTO getResult(Member member, String sessionId) {
        DepartmentProcessingState state = getState(member, sessionId);
        validateStateOwnership(state, member);

        if (!state.isComplete()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "모든 정보를 입력해야 결과를 조회할 수 있습니다.");
        }

        DepartmentTemplateResposneDTO responseDTO = callFastApiForResult(member, state);

        DepartmentTemplateRequestDTO requestDTO = DepartmentTemplateRequestDTO.builder()
                .bodyPart(state.getBodyPart())
                .selectedSign(state.getSelectedSign())
                .symptom(
                        SymptomRequest_2DTO.builder()
                                .intensity(state.getIntensity())
                                .startDate(state.getStartDate() != null ? LocalDate.parse(state.getStartDate()) : null)
                                .additional(state.getAdditional())
                                .build()
                )
                .build();

        DepartmentTemplate departmentTemplate = requestDTO.toEntity(member, sessionId)
                .toBuilder()
                .department(responseDTO.getDepartment())
                .departmentDescription(responseDTO.getDepartmentDescription())
                .questionsToDoctor(responseDTO.getQuestionsToDoctor())
                .build();

        departmentTemplateRepository.save(departmentTemplate);

        return DepartmentTemplateResposneDTO.fromEntity(departmentTemplate);
    }


    // fastapi 요청 메서드
    private DepartmentTemplateResposneDTO callFastApiForResult(Member member, DepartmentProcessingState state) {
        Language language = member.getBasicInfo().getLanguage();

        DepartmentTemplateRequestDTO requestDTO = DepartmentTemplateRequestDTO.builder()
                .language(language)
                .bodyPart(state.getBodyPart())
                .selectedSign(state.getSelectedSign())
                .symptom(
                        SymptomRequest_2DTO.builder()
                                .intensity(state.getIntensity())
                                .startDate(state.getStartDate() != null ? LocalDate.parse(state.getStartDate()) : null)
                                .additional(state.getAdditional())
                                .build()
                )
                .build();

        return fastApiCommunicationService.postToDepartmentTemplate(requestDTO, DepartmentTemplateResposneDTO.class);
    }



    private void validateStateOwnership(DepartmentProcessingState state, Member member) {
        if (state == null) {
            throw new BadRequestException(INVALID_PARAMETER, "세션이 만료되었습니다");
        }
        if (!state.getMemberId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED, "세션 소유자가 아닙니다");
        }
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
