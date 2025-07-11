package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.processingState.AIProcessingState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
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
        Map<String, String> healthInfoMap = new HashMap<>();

        if (state.getIsSelf()) {
            var healthInfo = member.getHealthInfo();
            healthInfoMap.put("past_history", healthInfo != null ? healthInfo.getPastHistory() : null);
            healthInfoMap.put("family_history", healthInfo != null ? healthInfo.getFamilyHistory() : null);
            healthInfoMap.put("now_medicine", healthInfo != null ? healthInfo.getNowMedicine() : null);
            healthInfoMap.put("allergy", healthInfo != null ? healthInfo.getAllergy() : null);
        } else {
            healthInfoMap.put("past_history", state.getPastHistory());
            healthInfoMap.put("family_history", state.getFamilyHistory());
            healthInfoMap.put("now_medicine", state.getNowMedicine());
            healthInfoMap.put("allergy", state.getAllergy());
        }

        healthInfoList.add(healthInfoMap);
        return healthInfoList;
    }

}
