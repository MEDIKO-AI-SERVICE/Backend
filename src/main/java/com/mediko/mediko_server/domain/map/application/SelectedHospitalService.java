package com.mediko.mediko_server.domain.map.application;

import com.mediko.mediko_server.domain.map.domain.SelectedHospital;
import com.mediko.mediko_server.domain.map.domain.repository.SelectedHospitalRepository;
import com.mediko.mediko_server.domain.map.dto.response.HospitalWithMapUrlDTO;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.recommend.domain.repository.HospitalRepository;
import com.mediko.mediko_server.domain.recommend.dto.response.GeocodeResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelectedHospitalService {

    private final SelectedHospitalRepository selectedHospitalRepository;
    private final HospitalRepository hospitalRepository;
    private final FlaskCommunicationService flaskCommunicationService;

    @Value("${app.name}")
    private String appName;

    @Transactional
    public HospitalWithMapUrlDTO getHospitalWithMapUrls(Long hospitalId, Member member) {
        Hospital hospital = findHospitalById(hospitalId);
        double userLat;
        double userLon;

        Double userLatitude = hospital.getUserLatitude();
        Double userLongitude = hospital.getUserLongitude();

        if (userLatitude != null && userLatitude != 0.0 &&
                userLongitude != null && userLongitude != 0.0) {
            userLat = userLatitude;
            userLon = userLongitude;
        }
        else {
            if (member != null && member.getBasicInfo() != null &&
                    member.getBasicInfo().getAddress() != null) {
                GeocodeResponseDTO coordinates = getCoordinatesFromAddress(
                        member.getBasicInfo().getAddress());
                userLat = coordinates.getLatitude();
                userLon = coordinates.getLongitude();
            } else {
                throw new IllegalArgumentException("사용자의 주소 정보가 없습니다.");
            }
        }

        var result = HospitalWithMapUrlDTO.fromEntity(
                HospitalResponseDTO.fromEntity(hospital),
                hospital,
                userLat,
                userLon,
                appName
        );

        saveSelectedHospitalIfNeeded(hospital, member, result.getMapUrls());
        return result;
    }

    private GeocodeResponseDTO getCoordinatesFromAddress(String address) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("address", address);
        return flaskCommunicationService.getAddressToCoords(requestData);
    }

    private Hospital findHospitalById(Long hospitalId) {
        return hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 병원을 찾을 수 없습니다."));
    }

    private void saveSelectedHospitalIfNeeded(Hospital hospital, Member member, MapUrlResponseDTO mapUrls) {
        if (member == null) {
            return;
        }

        SelectedHospital selectedHospital = SelectedHospital.builder()
                .hospital(hospital)
                .naverMap(mapUrls.getNaverMap())
                .kakaoMap(mapUrls.getKakaoMap())
                .googleMap(mapUrls.getGoogleMap())
                .build();

        selectedHospitalRepository.save(selectedHospital);
    }
}