package com.mediko.mediko_server.domain.report.application;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.openai.domain.Symptom;
import com.mediko.mediko_server.domain.report.domain.Report;
import com.mediko.mediko_server.domain.report.dto.response.ReportResponseDTO;
import com.mediko.mediko_server.global.s3.UuidFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReportMapper {

    public ReportResponseDTO toDTO(Report report) {
        return ReportResponseDTO.fromEntity(
                report,
                convertToBasicInfoMap(report.getMember().getBasicInfo()),
                convertToHealthInfoMap(report.getMember().getHealthInfo()),
                convertToBodyInfoMap(report.getSymptoms()),
                convertToSymptomInfoMap(report.getSymptoms()),
                convertToFileInfoMap(report.getSymptoms())
        );
    }

    private List<Map<String, Object>> convertToBasicInfoMap(BasicInfo basicInfo) {
        List<Map<String, Object>> basicInfoList = new ArrayList<>();
        basicInfoList.add(Map.of(
                "gender", basicInfo.getGender(),
                "age", basicInfo.getAge(),
                "height", basicInfo.getHeight(),
                "weight", basicInfo.getWeight()
        ));
        return basicInfoList;
    }

    private List<Map<String, String>> convertToHealthInfoMap(HealthInfo healthInfo) {
        List<Map<String, String>> healthInfoList = new ArrayList<>();
        healthInfoList.add(Map.of(
                "past_history", healthInfo.getPastHistory(),
                "family_history", healthInfo.getFamilyHistory(),
                "now_medicine", healthInfo.getNowMedicine(),
                "allergy", healthInfo.getAllergy()
        ));
        return healthInfoList;
    }

    private List<Map<String, Object>> convertToBodyInfoMap(Symptom symptom) {
        List<Map<String, Object>> bodyInfoList = new ArrayList<>();

        Map<String, Object> bodyInfo = new HashMap<>();
        bodyInfo.put("mbp_body", symptom.getSelectedMBPBodyParts());
        bodyInfo.put("sbp_body", symptom.getSelectedSBPBodyParts());
        bodyInfo.put("sign", symptom.getSelectedSigns());

        bodyInfoList.add(bodyInfo);

        return bodyInfoList;
    }

    private List<Map<String, String>> convertToSymptomInfoMap(Symptom symptom) {
        List<Map<String, String>> symptomInfoList = new ArrayList<>();

        if (symptom != null) {
            symptomInfoList.add(Map.of(
                    "start_value", String.valueOf(symptom.getStartValue()),
                    "start_unit", symptom.getStartUnit().toString(),
                    "duration_value", String.valueOf(symptom.getDurationValue()),
                    "duration_unit", symptom.getDurationUnit().toString(),
                    "intensity", String.valueOf(symptom.getIntensity()),
                    "additional", symptom.getAdditional() != null ? symptom.getAdditional() : ""
            ));
        }

        return symptomInfoList;
    }

    private List<Map<String, String>> convertToFileInfoMap(Symptom symptom) {
        List<Map<String, String>> fileInfoList = new ArrayList<>();

        if (symptom != null && symptom.getUuidFiles() != null && !symptom.getUuidFiles().isEmpty()) {
            for (UuidFile uuidFile : symptom.getUuidFiles()) {
                fileInfoList.add(Map.of(
                        "imgUrl", uuidFile.getFileUrl()
                ));
            }
        }

        return fileInfoList;
    }
}