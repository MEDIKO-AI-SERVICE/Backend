package com.mediko.mediko_server.domain.recommend.application.factory;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HospitalRequestFactory {
    public Map<String, Object> createFlaskRequest(
            BasicInfo basicInfo, HealthInfo healthInfo,
            Double userLatitude, Double userLongitude,
            String department, String suspectedDisease,
            boolean secondaryHospital, boolean tertiaryHospital
    ) {
        Map<String, Object> request = new HashMap<>();

        // 1. basic_info 맵 생성
        Map<String, Object> basicInfoMap = new HashMap<>();
        basicInfoMap.put("height", basicInfo.getHeight());
        basicInfoMap.put("age", basicInfo.getAge());
        basicInfoMap.put("language", basicInfo.getLanguage());
        basicInfoMap.put("number", basicInfo.getNumber());
        basicInfoMap.put("address", basicInfo.getAddress());
        basicInfoMap.put("gender", basicInfo.getGender());
        basicInfoMap.put("weight", basicInfo.getWeight());

        // 2. health_info 맵 생성
        Map<String, String> healthInfoMap = new HashMap<>();
        healthInfoMap.put("pastHistory", healthInfo.getPastHistory());
        healthInfoMap.put("allergy", healthInfo.getAllergy());
        healthInfoMap.put("familyHistory", healthInfo.getFamilyHistory());
        healthInfoMap.put("nowMedicine", healthInfo.getNowMedicine());

        // 3. 전체 요청 데이터 구성
        request.put("lat", userLatitude);
        request.put("lon", userLongitude);
        request.put("basic_info", basicInfoMap);
        request.put("health_info", healthInfoMap);
        request.put("department", department);
        request.put("suspected_disease", Arrays.asList(suspectedDisease.split(",")));  // 문자열을 리스트로 변환
        request.put("secondary_hospital", secondaryHospital);
        request.put("tertiary_hospital", tertiaryHospital);

        return request;
    }
}
