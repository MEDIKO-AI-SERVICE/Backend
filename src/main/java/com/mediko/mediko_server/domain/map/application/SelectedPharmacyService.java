package com.mediko.mediko_server.domain.map.application;

import com.mediko.mediko_server.domain.map.domain.SelectedPharmacy;
import com.mediko.mediko_server.domain.map.domain.repository.SelectedPharmacyRepostiroy;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.map.dto.response.PharmacyWithMapUrlDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.domain.recommend.domain.repository.PharmacyRepository;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
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

    public PharmacyWithMapUrlDTO getPharmacyWithMapUrls(Long pharmacyId, Member member) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약국을 찾을 수 없습니다."));

        MapUrlResponseDTO mapUrls = generateMapUrls(pharmacy);
        PharmacyResponseDTO pharmacyInfo = PharmacyResponseDTO.fromEntity(pharmacy);

        if (member != null) {
            saveSelectedPharmacy(pharmacy, member, mapUrls);
        }

        return new PharmacyWithMapUrlDTO(pharmacyInfo, mapUrls);
    }

    private void saveSelectedPharmacy(Pharmacy pharmacy, Member member, MapUrlResponseDTO mapUrls) {
        selectedPharmacyRepostiroy.save(
                SelectedPharmacy.builder()
                        .pharmacy(pharmacy)
                        .member(member)
                        .naverMap(mapUrls.getNaverMap())
                        .kakaoMap(mapUrls.getKakaoMap())
                        .googleMap(mapUrls.getGoogleMap())
                        .build()
        );
    }

    private MapUrlResponseDTO generateMapUrls(Pharmacy pharmacy) {
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

        return new MapUrlResponseDTO(naverMapUrl, kakaoMapUrl, googleMapUrl);
    }
}
