package com.mediko.mediko_server.domain.recommend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;

@Getter
public class GeocodeResponseDTO {

    @JsonProperty("lat")
    @JsonSerialize(using = ToStringSerializer.class)
    private Double latitude;

    @JsonProperty("lon")
    @JsonSerialize(using = ToStringSerializer.class)
    private Double longitude;

    public Double getLatitude() {
        return latitude != null ? Math.round(latitude * 1000000.0) / 1000000.0 : null;
    }

    public Double getLongitude() {
        return longitude != null ? Math.round(longitude * 1000000.0) / 1000000.0 : null;
    }
}
