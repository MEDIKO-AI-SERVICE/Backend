package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.processingState.AIProcessingState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AIReportMapper {

    // 기본 정보
    public List<Map<String, Object>> convertToBasicInfoMap(Member member, AIProcessingState state) {
        List<Map<String, Object>> basicInfoList = new ArrayList<>();
        if (state.getIsSelf()) {
            var basicInfo = member.getBasicInfo();
            basicInfoList.add(Map.of(
                    "gender", basicInfo.getGender(),
                    "age", basicInfo.getAge(),
                    "height", basicInfo.getHeight(),
                    "weight", basicInfo.getWeight()
            ));
        } else {
            basicInfoList.add(Map.of(
                    "gender", state.getGender(),
                    "age", state.getAge(),
                    "height", null,
                    "weight", null
            ));
        }
        return basicInfoList;
    }



    // 건강 정보
    public List<Map<String, String>> convertToHealthInfoMap(Member member, AIProcessingState state) {
        List<Map<String, String>> healthInfoList = new ArrayList<>();
        if (state.getIsSelf()) {
            var healthInfo = member.getHealthInfo();
            healthInfoList.add(Map.of(
                    "past_history", healthInfo != null ? healthInfo.getPastHistory() : null,
                    "family_history", healthInfo != null ? healthInfo.getFamilyHistory() : null,
                    "now_medicine", healthInfo != null ? healthInfo.getNowMedicine() : null,
                    "allergy", healthInfo != null ? healthInfo.getAllergy() : null
            ));
        } else {
            healthInfoList.add(Map.of(
                    "past_history", state.getPastHistory(),
                    "family_history", state.getFamilyHistory(),
                    "now_medicine", state.getNowMedicine(),
                    "allergy", state.getAllergy()
            ));
        }
        return healthInfoList;
    }
}
