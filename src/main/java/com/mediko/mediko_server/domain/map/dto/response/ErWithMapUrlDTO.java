package com.mediko.mediko_server.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErWithMapUrlDTO {

    @JsonProperty("er_info")
    private ErResponseDTO erInfo;

    @JsonProperty("map_urls")
    private MapUrlResponseDTO mapUrls;
}
