package com.mediko.mediko_server.domain.recommend.application.factory;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.filter.SortType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PharmacyRequestFactory {
    //request_1DTO
    public Map<String, Object> createFlaskRequest(
            BasicInfo basicInfo, Double userLatitude, Double userLongitude, Member member) {
        Map<String, Object> basicInfoMap = new HashMap<>();
        basicInfoMap.put("language", basicInfo.getLanguage().toString());
        basicInfoMap.put("number", basicInfo.getNumber());
        basicInfoMap.put("address", basicInfo.getAddress());
        basicInfoMap.put("gender", basicInfo.getGender().toString());
        basicInfoMap.put("age", basicInfo.getAge());
        basicInfoMap.put("height", basicInfo.getHeight());
        basicInfoMap.put("weight", basicInfo.getWeight());


        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("basic_info", basicInfoMap);
        requestMap.put("lat", userLatitude);
        requestMap.put("lon", userLongitude);
        requestMap.put("member_id", member.getId());

        return requestMap;
    }

    public Map<String, Object> createFlaskRequest(
            BasicInfo basicInfo, Double userLatitude, Double userLongitude,  SortType sortType,Member member) {
        Map<String, Object> basicInfoMap = new HashMap<>();
        basicInfoMap.put("language", basicInfo.getLanguage().toString());
        basicInfoMap.put("number", basicInfo.getNumber());
        basicInfoMap.put("address", basicInfo.getAddress());
        basicInfoMap.put("gender", basicInfo.getGender().toString());
        basicInfoMap.put("age", basicInfo.getAge());
        basicInfoMap.put("height", basicInfo.getHeight());
        basicInfoMap.put("weight", basicInfo.getWeight());


        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("basic_info", basicInfoMap);
        requestMap.put("lat", userLatitude);
        requestMap.put("lon", userLongitude);
        requestMap.put("member_id", member.getId());

        return requestMap;
    }
}
