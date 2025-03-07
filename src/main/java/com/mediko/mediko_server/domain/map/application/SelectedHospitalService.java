package com.mediko.mediko_server.domain.map.application;

import com.mediko.mediko_server.domain.map.domain.SelectedHospital;
import com.mediko.mediko_server.domain.map.domain.repository.SelectedHospitalRepository;
import com.mediko.mediko_server.domain.map.dto.response.HospitalWithMapUrlDTO;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.recommend.domain.repository.HospitalRepository;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelectedHospitalService {

    private final SelectedHospitalRepository selectedHospitalRepository;
    private final HospitalRepository hospitalRepository;

    @Value("${app.name}")
    private String appName;

    public HospitalWithMapUrlDTO getHospitalWithMapUrls(Long hospitalId, Member member) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 병원을 찾을 수 없습니다."));

        MapUrlResponseDTO mapUrls = generateMapUrls(hospital);
        HospitalResponseDTO hospitalInfo = HospitalResponseDTO.fromEntity(hospital);

        if (member != null) {
            saveSelectedHospital(hospital, member, mapUrls);
        }

        return new HospitalWithMapUrlDTO(hospitalInfo, mapUrls);
    }

    private void saveSelectedHospital(Hospital hospital, Member member, MapUrlResponseDTO mapUrls) {
        selectedHospitalRepository.save(
                SelectedHospital.builder()
                        .hospital(hospital)
                        .member(member)
                        .naverMap(mapUrls.getNaverMap())
                        .kakaoMap(mapUrls.getKakaoMap())
                        .googleMap(mapUrls.getGoogleMap())
                        .build()
        );
    }

    private MapUrlResponseDTO generateMapUrls(Hospital hospital) {
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


