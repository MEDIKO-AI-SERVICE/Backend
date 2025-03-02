package com.mediko.mediko_server.domain.report.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.openai.domain.Symptom;
import com.mediko.mediko_server.domain.openai.domain.repository.SymptomRepository;
import com.mediko.mediko_server.domain.report.domain.Report;
import com.mediko.mediko_server.domain.report.domain.repository.ReportRepository;
import com.mediko.mediko_server.domain.report.dto.request.ReportRequestDTO;
import com.mediko.mediko_server.domain.report.dto.response.ReportResponseDTO;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {
    private final BasicInfoRepository basicInfoRepository;
    private final SymptomRepository symptomRepository;
    private final ReportRepository reportRepository;
    private final FlaskCommunicationService flaskCommunicationService;

    @Transactional
    public ReportResponseDTO generateReport(ReportRequestDTO reportRequestDTO) {
        // 1. BasicInfo 및 Symptom 조회
        Optional<BasicInfo> basicInfo = basicInfoRepository.findById(reportRequestDTO.getBasicInfoId());
        Optional<Symptom> symptom = symptomRepository.findById(reportRequestDTO.getSymptomId());

        if (basicInfo.isEmpty() || symptom.isEmpty()) {
            throw new RuntimeException("BasicInfo or Symptom not found");
        }

        // 2. Symptom 및 BasicInfo 객체에서 필요한 값 추출
        Symptom symptomObj = symptom.get();
        BasicInfo basicInfoObj = basicInfo.get();

        // 3. 요청 데이터 생성
        String requestData = buildRequestData(symptomObj, basicInfoObj);

        // 4. Flask 서버로 요청 보내기
        ReportResponseDTO flaskResponse = flaskCommunicationService.getReportResponse(requestData);

        // 5. Report 객체 생성 전에 flaskResponse가 null이 아닌지 확인
        if (flaskResponse == null || flaskResponse.getRecommendedDepartment() == null) {
            throw new RuntimeException("Flask server response is invalid");
        }

        // 6. Report 객체 생성
        Report report = Report.builder()
                .recommendedDepartment(flaskResponse.getRecommendedDepartment())
                .possibleConditions(flaskResponse.getPossibleConditions())
                .questionsForDoctor(flaskResponse.getQuestionsForDoctor())
                .symptomChecklist(flaskResponse.getSymptomChecklist())
                .symptoms(symptomObj)
                .basicInfo(basicInfoObj)
                .member(basicInfoObj.getMember())
                .build();

        // 7. Report 저장
        reportRepository.save(report);

        // 8. ReportResponseDTO 반환
        return flaskResponse;
    }

    private String buildRequestData(Symptom symptomObj, BasicInfo basicInfoObj) {
        // Symptom 객체와 BasicInfo 객체에서 필요한 데이터 추출
        String macroBodyParts = extractMacroBodyParts(symptomObj);
        String microBodyParts = extractMicroBodyParts(symptomObj);
        String intensity = String.valueOf(symptomObj.getIntensity());
        String durationValue = String.valueOf(symptomObj.getDurationValue());
        String durationUnit = symptomObj.getDurationUnit().name();
        String startValue = String.valueOf(symptomObj.getStartValue());
        String startUnit = symptomObj.getStartUnit().name();
        String additionalInfo = symptomObj.getAdditional();
        String language = basicInfoObj.getLanguage().toString();

        // 요청 데이터 맵핑
        Map<String, Object> symptomDetails = new HashMap<>();
        symptomDetails.put("frequency", "intermittent");
        symptomDetails.put("intensity", intensity);
        symptomDetails.put("duration", durationValue + " " + durationUnit);
        symptomDetails.put("onset_time", startValue + " " + startUnit);

        Map<String, Object> symptomData = new HashMap<>();
        symptomData.put("macro_body_parts", macroBodyParts);
        symptomData.put("micro_body_parts", microBodyParts);
        symptomData.put("symptom_details", symptomDetails);
        symptomData.put("additional_info", additionalInfo);

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("symptoms", Collections.singletonList(symptomData));
        requestData.put("language", language);

        // JSON 변환
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(requestData);
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 중 오류 발생", e);
        }
    }

    private String extractMacroBodyParts(Symptom symptomObj) {
        // Symptom 객체에서 SelectedSBP의 body parts를 추출하여 JSON 형식으로 반환
        List<String> macroBodyPartsList = symptomObj.getSelectedSBPBodyParts(); // SelectedSBP의 body 추출
        return formatBodyParts(macroBodyPartsList);
    }

    private String extractMicroBodyParts(Symptom symptomObj) {
        // Symptom 객체에서 SelectedMBP의 body parts를 추출하여 JSON 형식으로 반환
        List<String> microBodyPartsList = symptomObj.getSelectedMBPBodyParts();
        return formatBodyParts(microBodyPartsList);
    }

    private String formatBodyParts(List<String> bodyPartsList) {
        // bodyParts 리스트를 JSON 형식으로 변환
        StringBuilder bodyParts = new StringBuilder("[");
        for (int i = 0; i < bodyPartsList.size(); i++) {
            bodyParts.append("\"").append(bodyPartsList.get(i)).append("\"");
            if (i < bodyPartsList.size() - 1) {
                bodyParts.append(",");
            }
        }
        bodyParts.append("]");
        return bodyParts.toString();
    }
}
