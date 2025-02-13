package com.mediko.mediko_server.domain.map.application;

import lombok.Getter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Getter
public class MapUrlGenerator {

    // Naver 지도 URL 생성
    public static String generateNaverMapUrl(
            double userLatitude, double userLongitude, // 사용자 위치
            double hpLatitude, double hpLongitude, // 병원 위치
            String hpName, String appName // 병원 이름, 앱 패키지명
    ) {
        return String.format(
                "nmap://route/walk?slat=%f&slng=%f&dlat=%f&dlng=%f&dname=%s&appname=%s",
                userLatitude, userLongitude, // 출발지: 사용자 위치
                hpLatitude, hpLongitude, // 도착지: 병원 위치
                URLEncoder.encode(hpName, StandardCharsets.UTF_8), // 병원 이름 인코딩
                URLEncoder.encode(appName, StandardCharsets.UTF_8) // 앱 패키지명 인코딩
        );
    }

    // Kakao 지도 URL 생성
    public static String generateKakaoMapUrl(
            double userLatitude, double userLongitude, // 사용자 위치
            double hpLatitude, double hpLongitude // 병원 위치
    ) {
        return String.format(
                "kakaomap://route?sp=%f,%f&ep=%f,%f&by=FOOT",
                userLatitude, userLongitude, // 출발지: 사용자 위치
                hpLatitude, hpLongitude // 도착지: 병원 위치
        );
    }

    // Google 지도 URL 생성
    public static String generateGoogleMapUrl(
            double userLatitude, double userLongitude, // 사용자 위치
            double hpLatitude, double hpLongitude // 병원 위치
    ) {
        return String.format(
                "https://www.google.com/maps/dir/?api=1&origin=%f,%f&destination=%f,%f&travelmode=walking",
                userLatitude, userLongitude, // 출발지: 사용자 위치
                hpLatitude, hpLongitude // 도착지: 병원 위치
        );
    }
}
