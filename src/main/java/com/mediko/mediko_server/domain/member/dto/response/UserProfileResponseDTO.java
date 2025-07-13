package com.mediko.mediko_server.domain.member.dto.response;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.infoType.HeightUnit;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.member.domain.infoType.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {
    
    private Map<String, Object> memberInfo;      // 회원정보
    private Map<String, Object> basicInfo;       // 기본정보
    private Map<String, Object> healthInfo;      // 건강정보
    private Map<String, Object> languageInfo;    // 언어정보
    
    public static UserProfileResponseDTO fromEntities(Member member, BasicInfo basicInfo, HealthInfo healthInfo) {
        // 회원정보 맵 생성
        Map<String, Object> memberInfoMap = new HashMap<>();
        memberInfoMap.put("name", member.getName());
        memberInfoMap.put("number", member.getNumber());
        memberInfoMap.put("email", member.getEmail());
        memberInfoMap.put("address", member.getAddress());
        memberInfoMap.put("loginId", member.getLoginId());
        
        // 기본정보 맵 생성
        Map<String, Object> basicInfoMap = new HashMap<>();
        if (basicInfo != null) {
            basicInfoMap.put("gender", basicInfo.getGender());
            basicInfoMap.put("age", basicInfo.getAge());
            basicInfoMap.put("height", basicInfo.getHeight());
            basicInfoMap.put("heightUnit", basicInfo.getHeightUnit());
            basicInfoMap.put("weight", basicInfo.getWeight());
            basicInfoMap.put("weightUnit", basicInfo.getWeightUnit());
        }
        
        // 건강정보 맵 생성
        Map<String, Object> healthInfoMap = new HashMap<>();
        if (healthInfo != null) {
            healthInfoMap.put("familyHistory", healthInfo.getFamilyHistory());
            healthInfoMap.put("pastHistory", healthInfo.getPastHistory());
            healthInfoMap.put("nowMedicine", healthInfo.getNowMedicine());
            healthInfoMap.put("allergy", healthInfo.getAllergy());
        }
        
        // 언어정보 맵 생성
        Map<String, Object> languageInfoMap = new HashMap<>();
        Language language = member.getLanguage();
        if (language != null) {
            languageInfoMap.put("language", language.getKoreanName());
        }
        
        return UserProfileResponseDTO.builder()
                .memberInfo(memberInfoMap)
                .basicInfo(basicInfoMap)
                .healthInfo(healthInfoMap)
                .languageInfo(languageInfoMap)
                .build();
    }
} 