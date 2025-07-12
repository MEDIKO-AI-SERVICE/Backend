package com.mediko.mediko_server.domain.map.application;

import com.mediko.mediko_server.domain.map.domain.SelectedPharmacy;
import com.mediko.mediko_server.domain.map.domain.repository.SelectedPharmacyRepostiroy;
import com.mediko.mediko_server.domain.map.dto.response.PharmacyWithMapUrlDTO;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.domain.recommend.domain.repository.PharmacyRepository;
import com.mediko.mediko_server.domain.recommend.dto.response.GeocodeResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelectedPharmacyService {

    private final SelectedPharmacyRepostiroy selectedPharmacyRepository;
    private final PharmacyRepository pharmacyRepository;
    private final FlaskCommunicationService flaskCommunicationService;

    @Value("${app.name}")
    private String appName;

    @Transactional
    public PharmacyWithMapUrlDTO getPharmacyWithMapUrls(Long pharmacyId, Member member) {
        Pharmacy pharmacy = findPharmacyById(pharmacyId);
        double userLat;
        double userLon;

        Double userLatitude = pharmacy.getUserLatitude();
        Double userLongitude = pharmacy.getUserLongitude();

        if (userLatitude != null && userLatitude != 0.0 &&
                userLongitude != null && userLongitude != 0.0) {
            userLat = userLatitude;
            userLon = userLongitude;
        }
        else {
            if (member != null && member.getAddress() != null) {
                GeocodeResponseDTO coordinates = getCoordinatesFromAddress(
                        member.getAddress());
                userLat = coordinates.getLatitude();
                userLon = coordinates.getLongitude();
            } else {
                throw new IllegalArgumentException("사용자의 주소 정보가 없습니다.");
            }
        }

        var result = PharmacyWithMapUrlDTO.fromEntity(
                PharmacyResponseDTO.fromEntity(pharmacy),
                pharmacy,
                userLat,
                userLon,
                appName
        );

        saveSelectedPharmacyIfNeeded(pharmacy, member, result.getMapUrls());
        return result;
    }

    private GeocodeResponseDTO getCoordinatesFromAddress(String address) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("address", address);
        return flaskCommunicationService.getAddressToCoords(requestData);
    }

    private Pharmacy findPharmacyById(Long pharmacyId) {
        return pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약국을 찾을 수 없습니다."));
    }

    private void saveSelectedPharmacyIfNeeded(Pharmacy pharmacy, Member member, MapUrlResponseDTO mapUrls) {
        if (member == null) {
            return;
        }

        SelectedPharmacy selectedPharmacy = SelectedPharmacy.builder()
                .member(member)
                .pharmacy(pharmacy)
                .naverMap(mapUrls.getNaverMap())
                .kakaoMap(mapUrls.getKakaoMap())
                .googleMap(mapUrls.getGoogleMap())
                .build();

        selectedPharmacyRepository.save(selectedPharmacy);
    }
}
