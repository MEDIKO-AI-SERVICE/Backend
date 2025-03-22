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
        List<Map<String, String>> symptomInfo = new ArrayList<>();
        if (report.getSymptoms() != null) {
            Map<String, String> info = new HashMap<>();
            info.put("intensity", String.valueOf(report.getSymptoms().getIntensity()));
            info.put("duration_value", String.valueOf(report.getSymptoms().getDurationValue()));
            info.put("duration_unit", report.getSymptoms().getDurationUnit().toString());
            info.put("start_value", String.valueOf(report.getSymptoms().getStartValue()));
            info.put("start_unit", report.getSymptoms().getStartUnit().toString());
            info.put("additional", report.getSymptoms().getAdditional() != null ?
                    report.getSymptoms().getAdditional() : "");
            symptomInfo.add(info);
        }

        // merge된 symptomInfo 생성
        List<Map<String, String>> mergedSymptomInfo = ReportResponseDTO.mergeSymptomInfo(symptomInfo);

        return ReportResponseDTO.fromEntity(
                report,
                convertToBasicInfoMap(report.getMember().getBasicInfo()),
                convertToHealthInfoMap(report.getMember().getHealthInfo()),
                convertToBodyInfoMap(report.getSymptoms()),
                mergedSymptomInfo,
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