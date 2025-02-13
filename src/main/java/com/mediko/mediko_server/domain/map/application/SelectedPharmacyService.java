package com.mediko.mediko_server.domain.map.application;

import com.mediko.mediko_server.domain.map.domain.SelectedPharmacy;
import com.mediko.mediko_server.domain.map.domain.repository.SelectedPharmacyRepostiroy;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.map.dto.response.SelectedPharmacyResponseDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.domain.recommend.domain.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelectedPharmacyService {
    private final SelectedPharmacyRepostiroy selectedPharmacyRepostiroy;
    private final PharmacyRepository pharmacyRepository;

    @Value("${app.name}")
    private String appName;

    // 약국 선택 및 저장
    public SelectedPharmacyResponseDTO saveSelectedPharmacy(Long pharmacyId, Member member, String appName) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약국을 찾을 수 없습니다."));

        double userLatitude = pharmacy.getUserLatitude();
        double userLongitude = pharmacy.getUserLongitude();
        double pharmacyLatitude = pharmacy.getPhLatitude();
        double pharmacyLongitude = pharmacy.getPhLongitude();
        String pharmacyName = pharmacy.getName();

        String naverMapUrl = MapUrlGenerator.generateNaverMapUrl(
                userLatitude, userLongitude, pharmacyLatitude, pharmacyLongitude, pharmacyName, appName
        );
        String kakaoMapUrl = MapUrlGenerator.generateKakaoMapUrl(
                userLatitude, userLongitude, pharmacyLatitude, pharmacyLongitude
        );
        String googleMapUrl = MapUrlGenerator.generateGoogleMapUrl(
                userLatitude, userLongitude, pharmacyLatitude, pharmacyLongitude
        );

        SelectedPharmacy selectedPharmacy = selectedPharmacyRepostiroy.save(
                SelectedPharmacy.builder()
                        .pharmacy(pharmacy)
                        .member(member)
                        .naverMap(naverMapUrl)
                        .kakaoMap(kakaoMapUrl)
                        .googleMap(googleMapUrl)
                        .build()
        );

        return SelectedPharmacyResponseDTO.fromEntity(pharmacy, selectedPharmacy);
    }

    // 지도 URL 조회
    public MapUrlResponseDTO getMapUrlsForPharmacy(Long pharamacyId) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharamacyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약국을 찾을 수 없습니다."));

        double userLatitude = pharmacy.getUserLatitude();
        double userLongitude = pharmacy.getUserLongitude();
        double PharmacyLatitude = pharmacy.getPhLatitude();
        double PharmacyLongitude = pharmacy.getPhLongitude();
        String pharmacyName = pharmacy.getName();

        String naverMapUrl = MapUrlGenerator.generateNaverMapUrl(
                userLatitude, userLongitude, PharmacyLatitude, PharmacyLongitude, pharmacyName, appName
        );
        String kakaoMapUrl = MapUrlGenerator.generateKakaoMapUrl(
                userLatitude, userLongitude, PharmacyLatitude, PharmacyLongitude
        );
        String googleMapUrl = MapUrlGenerator.generateGoogleMapUrl(
                userLatitude, userLongitude, PharmacyLatitude, PharmacyLongitude
        );

        return new MapUrlResponseDTO(naverMapUrl, kakaoMapUrl, googleMapUrl);
    }
}
