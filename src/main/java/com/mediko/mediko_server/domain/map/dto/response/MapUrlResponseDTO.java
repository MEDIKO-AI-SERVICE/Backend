package com.mediko.mediko_server.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MapUrlResponseDTO {

    @JsonProperty("naver_map")
    private String naverMap;

    @JsonProperty("kakao_map")
    private String kakaoMap;

    @JsonProperty("google_map")
    private String googleMap;
}
