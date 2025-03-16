package com.mediko.mediko_server.domain.map.application;

import lombok.Getter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Getter
public class MapUrlGenerator {

    // Naver 지도 URL 생성
    public static String generateNaverMapUrl(
            double userLatitude, double userLongitude,
            double destinationLatitude, double destinationLongitude,
            String destinationName, String appName
    ) {
        return String.format(
                "nmap://route/walk?slat=%f&slng=%f&dlat=%f&dlng=%f&dname=%s&appname=%s",
                userLatitude, userLongitude,
                destinationLatitude, destinationLongitude,
                URLEncoder.encode(destinationName, StandardCharsets.UTF_8),
                URLEncoder.encode(appName, StandardCharsets.UTF_8)
        );
    }

    // Kakao 지도 URL 생성
    public static String generateKakaoMapUrl(
            double userLatitude, double userLongitude,
            double destinationLatitude, double destinationLongitude
    ) {
        return String.format(
                "kakaomap://route?sp=%f,%f&ep=%f,%f&by=FOOT",
                userLatitude, userLongitude,
                destinationLatitude, destinationLongitude
        );
    }

    // Google 지도 URL 생성
    public static String generateGoogleMapUrl(
            double userLatitude, double userLongitude,
            double destinationLatitude, double destinationLongitude
    ) {
        return String.format(
                "https://www.google.com/maps/dir/?api=1&origin=%f,%f&destination=%f,%f&travelmode=walking",
                userLatitude, userLongitude,
                destinationLatitude, destinationLongitude
        );
    }
}
