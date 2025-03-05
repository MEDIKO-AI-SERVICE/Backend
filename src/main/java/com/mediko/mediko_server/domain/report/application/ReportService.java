package com.mediko.mediko_server.domain.report.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
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
import java.util.stream.Collectors;

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
    public ReportResponseDTO generateReport(ReportRequestDTO reportRequestDTO, Member member) {

        Optional<Symptom> symptom = symptomRepository.findById(reportRequestDTO.getSymptomId());

        if (symptom.isEmpty()) { throw new RuntimeException("Symptom not found"); }

        Symptom symptomObj = symptom.get();
        Map<String, Object> requestData = buildRequestData(symptomObj, member);

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
                .member(member)
                .build();

        Report savedReport = reportRepository.save(report);

        return reportMapper.toDTO(savedReport);
    }


    // 단일 리포트 조회
    public ReportResponseDTO getReport(Long reportId, Member member) {
        Report report = reportRepository.findByIdAndMember(reportId, member)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        return reportMapper.toDTO(report);
    }

    // 회원별 전체 리포트 조회
    public List<ReportResponseDTO> getAllReportsByMember(Member member) {
        List<Report> reports = reportRepository.findAllByMemberOrderByCreatedAtDesc(member);
        return reports.stream()
                .map(reportMapper::toDTO)
                .collect(Collectors.toList());
    }


    // FLASK 서버로 전송할 요청 데이터 빌드
    private Map<String, Object> buildRequestData(Symptom symptomObj, Member member) {
        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("BasicInfo not found for member"));

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
        requestData.put("language", basicInfo.getLanguage().toString());

        return requestData;
    }
}
