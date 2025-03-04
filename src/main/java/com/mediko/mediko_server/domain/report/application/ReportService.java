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
    private final ReportMapper reportMapper;

    // AI 문진 생성
    @Transactional
    public ReportResponseDTO generateReport(ReportRequestDTO reportRequestDTO) {
        Optional<BasicInfo> basicInfo = basicInfoRepository.findById(reportRequestDTO.getBasicInfoId());
        Optional<Symptom> symptom = symptomRepository.findById(reportRequestDTO.getSymptomId());

        if (basicInfo.isEmpty() || symptom.isEmpty()) {
            throw new RuntimeException("BasicInfo or Symptom not found");
        }

        Symptom symptomObj = symptom.get();
        BasicInfo basicInfoObj = basicInfo.get();

        Map<String, Object> requestData = buildRequestData(symptomObj, basicInfoObj);

        ReportResponseDTO flaskResponse = flaskCommunicationService.getReportResponse(requestData);

        if (flaskResponse == null || flaskResponse.getRecommendedDepartment() == null) {
            throw new RuntimeException("Flask server response is invalid");
        }

        Report report = Report.builder()
                .recommendedDepartment(flaskResponse.getRecommendedDepartment())
                .possibleConditions(flaskResponse.getPossibleConditions())
                .questionsForDoctor(flaskResponse.getQuestionsForDoctor())
                .symptomChecklist(flaskResponse.getSymptomChecklist())
                .symptoms(symptomObj)
                .basicInfo(basicInfoObj)
                .member(basicInfoObj.getMember())
                .build();

        Report savedReport = reportRepository.save(report);

        return reportMapper.toDTO(savedReport);
    }


    // FLASK 서버로 전송할 요청 데이터 빌드
    private Map<String, Object> buildRequestData(Symptom symptomObj, BasicInfo basicInfoObj) {
        List<String> macroBodyParts = symptomObj.getSelectedSBPBodyParts();
        List<String> microBodyParts = symptomObj.getSelectedMBPBodyParts();

        Map<String, Object> symptomDetails = new HashMap<>();
        symptomDetails.put("frequency", "intermittent");
        symptomDetails.put("intensity", symptomObj.getIntensity());
        symptomDetails.put("duration", symptomObj.getDurationValue() + " " + symptomObj.getDurationUnit().name());
        symptomDetails.put("onset_time", symptomObj.getStartValue() + " " + symptomObj.getStartUnit().name());

        Map<String, Object> symptomData = new HashMap<>();
        symptomData.put("macro_body_parts", macroBodyParts);
        symptomData.put("micro_body_parts", microBodyParts);
        symptomData.put("symptom_details", symptomDetails);
        symptomData.put("additional_info", symptomObj.getAdditional());

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("symptoms", Collections.singletonList(symptomData));
        requestData.put("language", basicInfoObj.getLanguage().toString());

        return requestData;
    }
}
