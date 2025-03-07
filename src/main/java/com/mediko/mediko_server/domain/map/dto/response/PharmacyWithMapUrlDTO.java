package com.mediko.mediko_server.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyWithMapUrlDTO {

    @JsonProperty("pharmacy_info")
    private PharmacyResponseDTO pharmacyInfo;

    @JsonProperty("map_urls")
    private MapUrlResponseDTO mapUrls;
}
