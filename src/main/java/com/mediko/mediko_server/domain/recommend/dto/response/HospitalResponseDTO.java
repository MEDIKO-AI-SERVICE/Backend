package com.mediko.mediko_server.domain.recommend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HospitalResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("department")
    private String hpDepartment;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("address")
    private String hpAddress;

    @JsonProperty("latitude")
    private Double hpLatitude;

    @JsonProperty("longitude")
    private Double hpLongitude;

    @JsonProperty("location")
    private List<Double> latLon;

    @JsonProperty("sidocdnm")
    private String sidocdnm;

    @JsonProperty("sggucdnm")
    private String sggucdnm;

    @JsonProperty("emdongnm")
    private String emdongnm;

    @JsonProperty("clcdnm")
    private String clcdnm;

    @JsonProperty("es_distance_in_km")
    private Double esDistanceInKm;

    @JsonProperty("transit_travel_distance_km")
    private Double travelKm;

    @JsonProperty("transit_travel_time_h")
    private Integer travelH;

    @JsonProperty("transit_travel_time_m")
    private Integer travelM;

    @JsonProperty("transit_travel_time_s")
    private Integer travelS;

    @JsonProperty("sort_score")
    private Long sortScore;

    @JsonProperty("similarity")
    private Double similarity;

    @JsonProperty("url")
    private String url;

    public static HospitalResponseDTO fromEntity(Hospital hospital) {
        return new HospitalResponseDTO(
                hospital.getId(),
                hospital.getName(),
                hospital.getHpDepartment(),
                hospital.getTelephone(),
                hospital.getHpAddress(),
                hospital.getHpLatitude(),
                hospital.getHpLongitude(),
                hospital.getLatLon(),
                hospital.getSidocdnm(),
                hospital.getSggucdnm(),
                hospital.getEmdongnm(),
                hospital.getClcdnm(),
                hospital.getEsDistanceInKm(),
                hospital.getTravelKm(),
                hospital.getTravelH(),
                hospital.getTravelM(),
                hospital.getTravelS(),
                hospital.getSortScore(),
                hospital.getSimilarity(),
                hospital.getUrl()
        );
    }
}