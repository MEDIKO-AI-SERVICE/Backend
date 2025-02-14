package com.mediko.mediko_server.domain.recommend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeocodeResponseDTO {

    @JsonProperty("lat")
    private Double latitude;

    @JsonProperty("lon")
    private Double longitude;
}
