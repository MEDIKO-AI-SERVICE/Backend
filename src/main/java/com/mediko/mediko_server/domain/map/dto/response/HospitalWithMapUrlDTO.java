package com.mediko.mediko_server.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.map.application.MapUrlGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HospitalWithMapUrlDTO {

    @JsonProperty("hospital_info")
    private HospitalResponseDTO hospitalInfo;

    @JsonProperty("map_urls")
    private MapUrlResponseDTO mapUrls;


    public static HospitalWithMapUrlDTO fromEntity(
            HospitalResponseDTO hospitalResponseDTO, Hospital hospital,
            double userLat, double userLon, String appName) {

        String naverMapUrl = MapUrlGenerator.generateNaverMapUrl(
                userLat, userLon,
                hospital.getHpLatitude(), hospital.getHpLongitude(),
                hospital.getName(), appName);

        String kakaoMapUrl = MapUrlGenerator.generateKakaoMapUrl(
                userLat, userLon,
                hospital.getHpLatitude(), hospital.getHpLongitude());

        String googleMapUrl = MapUrlGenerator.generateGoogleMapUrl(
                userLat, userLon,
                hospital.getHpLatitude(), hospital.getHpLongitude());

        MapUrlResponseDTO mapUrls = new MapUrlResponseDTO(naverMapUrl, kakaoMapUrl, googleMapUrl);

        return HospitalWithMapUrlDTO.builder()
                .hospitalInfo(hospitalResponseDTO)
                .mapUrls(mapUrls)
                .build();
    }
}
