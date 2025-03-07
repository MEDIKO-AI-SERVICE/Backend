package com.mediko.mediko_server.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HospitalWithMapUrlDTO {

    @JsonProperty("hospital_info")
    private HospitalResponseDTO hospitalInfo;

    @JsonProperty("map_urls")
    private MapUrlResponseDTO mapUrls;
}
