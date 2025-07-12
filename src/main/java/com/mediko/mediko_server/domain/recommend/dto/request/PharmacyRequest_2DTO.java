package com.mediko.mediko_server.domain.recommend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.domain.recommend.domain.filter.SortType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyRequest_2DTO {

    @JsonProperty("sort_type")
    private SortType sortType;

    @JsonProperty("lat")
    private Double userLatitude;

    @JsonProperty("lon")
    private Double userLongitude;

    public Pharmacy toEntity() {
        return Pharmacy.builder()
                .sortType(this.sortType)
                .userLatitude(this.userLatitude)
                .userLongitude(this.userLongitude)
                .build();
    }
}
