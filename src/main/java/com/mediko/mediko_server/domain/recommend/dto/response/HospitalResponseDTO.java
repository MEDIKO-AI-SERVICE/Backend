package com.mediko.mediko_server.domain.recommend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HospitalResponseDTO {

    @JsonProperty("id")
    private Long hpId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("department")
    private String department;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("es_distance_in_km")
    private Double esDistanceInKm;

    @JsonProperty("sidocdnm")
    private String sidocdnm;

    @JsonProperty("sggucdnm")
    private String sggucdnm;

    @JsonProperty("emdongnm")
    private String emdongnm;

    @JsonProperty("clcdnm")
    private String clcdnm;

    @JsonProperty("url")
    private String url;

    @JsonProperty("sort_score")
    private Long sortScore;

    @JsonProperty("department_match")
    private Boolean departmentMatch;

    @JsonProperty("location")
    @Transient
    private List<Double> latLon;

    @JsonProperty("similarity")
    private Double similarity;

    @JsonProperty("transit_travel_distance_km")
    private Double travelKm;

    @JsonProperty("transit_travel_time_h")
    private Integer travelH;

    @JsonProperty("transit_travel_time_m")
    private Integer travelM;

    @JsonProperty("transit_travel_time_s")
    private Integer travelS;


    public static HospitalResponseDTO fromEntity(Hospital hospital) {
        return new HospitalResponseDTO(
                hospital.getHpId(),
                hospital.getName(),
                hospital.getAddress(),
                hospital.getTelephone(),
                hospital.getDepartment(),
                hospital.getLatitude(),
                hospital.getLongitude(),
                hospital.getEsDistanceInKm(),
                hospital.getSidocdnm(),
                hospital.getSggucdnm(),
                hospital.getEmdongnm(),
                hospital.getClcdnm(),
                hospital.getUrl(),
                hospital.getSortScore(),
                hospital.isDepartmentMatch(),
                hospital.getLatLon(),
                hospital.getSimilarity(),
                hospital.getTravelKm(),
                hospital.getTravelH(),
                hospital.getTravelM(),
                hospital.getTravelS()
        );
    }
}