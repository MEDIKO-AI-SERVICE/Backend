package com.mediko.mediko_server.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.map.application.MapUrlGenerator;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErWithMapUrlDTO {

    @JsonProperty("er_info")
    private ErResponseDTO erInfo;

    @JsonProperty("map_urls")
    private MapUrlResponseDTO mapUrls;


    public static ErWithMapUrlDTO fromEntity(
            ErResponseDTO erResponseDTO, Er er, double userLat, double userLon, String appName) {

        String naverMapUrl = MapUrlGenerator.generateNaverMapUrl(
                userLat, userLon,
                er.getErLatitude(), er.getErLongitude(),
                er.getName(), appName);

        String kakaoMapUrl = MapUrlGenerator.generateKakaoMapUrl(
                userLat, userLon,
                er.getErLatitude(), er.getErLongitude());

        String googleMapUrl = MapUrlGenerator.generateGoogleMapUrl(
                userLat, userLon,
                er.getErLatitude(), er.getErLongitude());

        MapUrlResponseDTO mapUrls = new MapUrlResponseDTO(naverMapUrl, kakaoMapUrl, googleMapUrl);

        return ErWithMapUrlDTO.builder()
                .erInfo(erResponseDTO)
                .mapUrls(mapUrls)
                .build();
    }
}
