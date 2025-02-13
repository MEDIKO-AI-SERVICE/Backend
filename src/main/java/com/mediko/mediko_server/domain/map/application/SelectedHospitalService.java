package com.mediko.mediko_server.domain.map.application;

import com.mediko.mediko_server.domain.map.domain.SelectedHospital;
import com.mediko.mediko_server.domain.map.domain.repository.SelectedHospitalRepository;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.map.dto.response.SelectedHospitalResponseDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.recommend.domain.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class SelectedHospitalService {

    private final SelectedHospitalRepository selectedHospitalRepository;
    private final HospitalRepository hospitalRepository;

    @Value("${app.name}")
    private String appName;

    // 병원 선택 및 저장
    public SelectedHospitalResponseDTO saveSelectedHospital(Long hospitalId, Member member, String appName) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 병원을 찾을 수 없습니다."));

        double userLatitude = hospital.getUserLatitude();
        double userLongitude = hospital.getUserLongitude();
        double hospitalLatitude = hospital.getHpLatitude();
        double hospitalLongitude = hospital.getHpLongitude();
        String hospitalName = hospital.getName();

        String naverMapUrl = MapUrlGenerator.generateNaverMapUrl(
                userLatitude, userLongitude, hospitalLatitude, hospitalLongitude, hospitalName, appName
        );
        String kakaoMapUrl = MapUrlGenerator.generateKakaoMapUrl(
                userLatitude, userLongitude, hospitalLatitude, hospitalLongitude
        );
        String googleMapUrl = MapUrlGenerator.generateGoogleMapUrl(
                userLatitude, userLongitude, hospitalLatitude, hospitalLongitude
        );

        SelectedHospital selectedHospital = selectedHospitalRepository.save(
                SelectedHospital.builder()
                        .hospital(hospital)
                        .member(member)
                        .naverMap(naverMapUrl)
                        .kakaoMap(kakaoMapUrl)
                        .googleMap(googleMapUrl)
                        .build()
        );

        return SelectedHospitalResponseDTO.fromEntity(hospital, selectedHospital);
    }

    // 지도 URL 조회
    public MapUrlResponseDTO getMapUrlsForHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 병원을 찾을 수 없습니다."));

        double userLatitude = hospital.getUserLatitude();
        double userLongitude = hospital.getUserLongitude();
        double hospitalLatitude = hospital.getHpLatitude();
        double hospitalLongitude = hospital.getHpLongitude();
        String hospitalName = hospital.getName();

        String naverMapUrl = MapUrlGenerator.generateNaverMapUrl(
                userLatitude, userLongitude, hospitalLatitude, hospitalLongitude, hospitalName, appName
        );
        String kakaoMapUrl = MapUrlGenerator.generateKakaoMapUrl(
                userLatitude, userLongitude, hospitalLatitude, hospitalLongitude
        );
        String googleMapUrl = MapUrlGenerator.generateGoogleMapUrl(
                userLatitude, userLongitude, hospitalLatitude, hospitalLongitude
        );

        return new MapUrlResponseDTO(naverMapUrl, kakaoMapUrl, googleMapUrl);
    }
}


