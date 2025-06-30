//package com.mediko.mediko_server.domain.report.application;
//
//import com.mediko.mediko_server.domain.member.domain.BasicInfo;
//import com.mediko.mediko_server.domain.member.domain.Member;
//import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
//import com.mediko.mediko_server.domain.report.domain.Report;
//import com.mediko.mediko_server.domain.report.domain.repository.ReportRepository;
//import com.mediko.mediko_server.domain.report.dto.response.ReportResponseDTO;
//import com.mediko.mediko_server.domain.openai.domain.Symptom;
//import com.mediko.mediko_server.domain.report.dto.request.ReportRequestDTO;
//import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class ReportService {
//
//    private final ReportRepository reportRepository;
//    private final ReportMapper reportMapper;
//    private final FlaskCommunicationService flaskCommunicationService;
//    private final BasicInfoRepository basicInfoRepository;
//
//    // AI 문진 생성
//    @Transactional
//    public Map<String, Object> generateReport(ReportRequestDTO reportRequestDTO, Member member) {
//        try {
//            Symptom symptom = symptomRepository.findById(reportRequestDTO.getSymptomId())
//                    .orElseThrow(() -> new RuntimeException("Symptom not found"));
//
//            BasicInfo basicInfo = basicInfoRepository.findByMember(member)
//                    .orElseThrow(() -> new RuntimeException("BasicInfo not found"));
//            String userLanguage = basicInfo.getLanguage().toString();
//
//            Map<String, Object> requestData = buildRequestData(symptom, member);
//            ReportResponseDTO flaskResponse = flaskCommunicationService.getReportResponse(requestData);
//
//            if (flaskResponse == null || flaskResponse.getRecommendedDepartment() == null) {
//                log.error("Flask server response is invalid: {}", flaskResponse);
//                throw new RuntimeException("Flask server response is invalid");
//            }
//
//            Report report = Report.builder()
//                    .recommendedDepartment(flaskResponse.getRecommendedDepartment())
//                    .possibleConditions(flaskResponse.getPossibleConditions())
//                    .questionsForDoctor(flaskResponse.getQuestionsForDoctor())
//                    .symptomChecklist(flaskResponse.getSymptomChecklist())
//                    .symptoms(symptom)
//                    .member(member)
//                    .build();
//
//            Report savedReport;
//            try {
//                savedReport = reportRepository.saveAndFlush(report);
//            } catch (Exception e) {
//                log.error("Error saving report: ", e);
//                throw new RuntimeException("Failed to save report", e);
//            }
//
//            ReportResponseDTO baseDTO = reportMapper.toDTO(savedReport);
//            ReportResponseDTO patientResponse = baseDTO.convertToPatientResponse(userLanguage);
//            ReportResponseDTO doctorResponse = baseDTO.convertToDoctorResponse();
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("patient", patientResponse);
//            response.put("doctor", doctorResponse);
//
//            log.info("📋 Final Response to Client: {}", response);
//
//            return response;
//        } catch (Exception e) {
//            log.error("Error in generateReport: ", e);
//            throw new RuntimeException("Failed to generate report", e);
//        }
//    }
//
//    // 단일 문진 조회 (환자용만)
//    public ReportResponseDTO getPatientReport(Long reportId, Member member) {
//        Report report = findReport(reportId, member);
//        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
//                .orElseThrow(() -> new RuntimeException("BasicInfo not found"));
//        String userLanguage = basicInfo.getLanguage().toString();
//
//        return reportMapper.toDTO(report).convertToPatientResponse(userLanguage);
//    }
//
//    // 단일 문진 조회 (의사용만)
//    public ReportResponseDTO getDoctorReport(Long reportId, Member member) {
//        Report report = findReport(reportId, member);
//        return reportMapper.toDTO(report).convertToDoctorResponse();
//    }
//
//    // 단일 문진 조회
//    public Map<String, Object> getReportForPatientAndDoctor(Long reportId, Member member) {
//        Report report = findReport(reportId, member);
//        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
//                .orElseThrow(() -> new RuntimeException("BasicInfo not found"));
//        String userLanguage = basicInfo.getLanguage().toString();
//
//        ReportResponseDTO baseDTO = reportMapper.toDTO(report);
//
//        ReportResponseDTO patientResponse = baseDTO.convertToPatientResponse(userLanguage);
//        ReportResponseDTO doctorResponse = baseDTO.convertToDoctorResponse();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("patient", patientResponse);
//        response.put("doctor", doctorResponse);
//
//        return response;
//    }
//
//    // 단일 문진 조회 공통 메서드
//    private Report findReport(Long reportId, Member member) {
//        return reportRepository.findByIdAndMember(reportId, member)
//                .orElseThrow(() -> new RuntimeException("Report not found"));
//    }
//
//    // 회원별 문진 리스트 조회
//    public List<Map<String, Object>> getAllReportsByMember(Member member) {
//        List<Report> reports = reportRepository.findAllByMemberOrderByCreatedAtDesc(member);
//        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
//                .orElseThrow(() -> new RuntimeException("BasicInfo not found"));
//        String userLanguage = basicInfo.getLanguage().toString();
//
//        return reports.stream()
//                .map(report -> {
//                    ReportResponseDTO baseDTO = reportMapper.toDTO(report);
//                    ReportResponseDTO patientResponse = baseDTO.convertToPatientResponse(userLanguage);
//                    ReportResponseDTO doctorResponse = baseDTO.convertToDoctorResponse();
//
//                    Map<String, Object> response = new HashMap<>();
//                    response.put("patient", patientResponse);
//                    response.put("doctor", doctorResponse);
//                    return response;
//                })
//                .collect(Collectors.toList());
//    }
//
//    // FLASK 서버로 전송할 요청 데이터 빌드
//    private Map<String, Object> buildRequestData(Symptom symptomObj, Member member) {
//        log.info("📝 Building request data for Flask - Symptom ID: {}", symptomObj.getId());
//
//        BasicInfo basicInfo = basicInfoRepository.findByMember(member)
//                .orElseThrow(() -> new RuntimeException("BasicInfo not found for member"));
//
//        List<String> macroBodyParts = symptomObj.getSelectedSBPBodyParts();
//        List<String> microBodyParts = symptomObj.getSelectedMBPBodyParts();
//
//        log.info("🔍 Selected body parts - Macro: {}, Micro: {}", macroBodyParts, microBodyParts);
//
//        Map<String, Object> symptomDetails = new HashMap<>();
//        symptomDetails.put("frequency", "intermittent");
//        symptomDetails.put("intensity", symptomObj.getIntensity());
//        symptomDetails.put("duration", symptomObj.getDurationValue() + " " + symptomObj.getDurationUnit().name());
//        symptomDetails.put("onset_time", symptomObj.getStartValue() + " " + symptomObj.getStartUnit().name());
//
//        log.info("📊 Symptom details: {}", symptomDetails);
//
//        Map<String, Object> symptomData = new HashMap<>();
//        symptomData.put("macro_body_parts", macroBodyParts);
//        symptomData.put("micro_body_parts", microBodyParts);
//        symptomData.put("symptom_details", symptomDetails);
//        symptomData.put("additional_info", symptomObj.getAdditional());
//
//        Map<String, Object> requestData = new HashMap<>();
//        requestData.put("symptoms", Collections.singletonList(symptomData));
//        requestData.put("language", basicInfo.getLanguage().toString());
//
//        log.info("📤 Final request data to Flask: {}", requestData);
//
//        return requestData;
//    }
//}