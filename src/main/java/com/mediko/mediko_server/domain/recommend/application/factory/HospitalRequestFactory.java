package com.mediko.mediko_server.domain.recommend.application.factory;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HospitalRequestFactory {
    public Map<String, Object> createFlaskRequest(
            BasicInfo basicInfo, HealthInfo healthInfo,
            Double userLatitude, Double userLongitude,
            String department, List<String> suspectedDisease,
            boolean secondaryHospital, boolean tertiaryHospital, Member member) {

        // 1. basic_info 맵 생성
        Map<String, Object> basicInfoMap = new HashMap<>();
        basicInfoMap.put("height", basicInfo.getHeight());
        basicInfoMap.put("age", basicInfo.getAge());
        basicInfoMap.put("language", member.getLanguage());
        basicInfoMap.put("number", member.getNumber());
        basicInfoMap.put("address", member.getAddress());
        basicInfoMap.put("gender", basicInfo.getGender());
        basicInfoMap.put("weight", basicInfo.getWeight());

        // 2. health_info 맵 생성
        Map<String, String> healthInfoMap = new HashMap<>();
        healthInfoMap.put("pastHistory", healthInfo.getPastHistory());
        healthInfoMap.put("allergy", healthInfo.getAllergy());
        healthInfoMap.put("familyHistory", healthInfo.getFamilyHistory());
        healthInfoMap.put("nowMedicine", healthInfo.getNowMedicine());

        // 3. 전체 요청 데이터 구성
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("lat", userLatitude);
        requestMap.put("lon", userLongitude);
        requestMap.put("basic_info", basicInfoMap);
        requestMap.put("health_info", healthInfoMap);
        requestMap.put("department", department);
        requestMap.put("suspected_disease", suspectedDisease);
        requestMap.put("secondary_hospital", secondaryHospital);
        requestMap.put("tertiary_hospital", tertiaryHospital);
        requestMap.put("member_id", member.getId());

        return requestMap;
    }
}
