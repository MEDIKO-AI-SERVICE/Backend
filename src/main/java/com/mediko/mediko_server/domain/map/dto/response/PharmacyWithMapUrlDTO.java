package com.mediko.mediko_server.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.domain.map.application.MapUrlGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyWithMapUrlDTO {

    @JsonProperty("pharmacy_info")
    private PharmacyResponseDTO pharmacyInfo;

    @JsonProperty("map_urls")
    private MapUrlResponseDTO mapUrls;


    public static PharmacyWithMapUrlDTO fromEntity(
            PharmacyResponseDTO pharmacyResponseDTO, Pharmacy pharmacy,
            double userLat, double userLon, String appName) {

        String naverMapUrl = MapUrlGenerator.generateNaverMapUrl(
                userLat, userLon, pharmacy.getPhLatitude(), pharmacy.getPhLongitude(),
                pharmacy.getName(), appName);

        String kakaoMapUrl = MapUrlGenerator.generateKakaoMapUrl(
                userLat, userLon,
                pharmacy.getPhLatitude(), pharmacy.getPhLongitude());

        String googleMapUrl = MapUrlGenerator.generateGoogleMapUrl(
                userLat, userLon,
                pharmacy.getPhLatitude(), pharmacy.getPhLongitude());

        MapUrlResponseDTO mapUrls = new MapUrlResponseDTO(naverMapUrl, kakaoMapUrl, googleMapUrl);

        return PharmacyWithMapUrlDTO.builder()
                .pharmacyInfo(pharmacyResponseDTO)
                .mapUrls(mapUrls)
                .build();
    }
}
