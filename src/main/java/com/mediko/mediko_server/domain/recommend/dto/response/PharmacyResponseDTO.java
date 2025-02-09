package com.mediko.mediko_server.domain.recommend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyResponseDTO {
    @JsonProperty("id")
    private Long phId;

    @JsonProperty("dutymapimg")
    private String maping;

    @JsonProperty("dutyname")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("dutytel1")
    private String tel;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("transit_travel_distance_km")
    private Double travelKm;

    @JsonProperty("transit_travel_time_h")
    private Integer travelH;

    @JsonProperty("transit_travel_time_m")
    private Integer travelM;

    @JsonProperty("transit_travel_time_s")
    private Integer travelS;

    @JsonProperty("dutytime1s")
    private String start1;

    @JsonProperty("dutytime2s")
    private String start2;

    @JsonProperty("dutytime3s")
    private String start3;

    @JsonProperty("dutytime4s")
    private String start4;

    @JsonProperty("dutytime5s")
    private String start5;

    @JsonProperty("dutytime6s")
    private String start6;

    @JsonProperty("dutytime7s")
    private String start7;

    @JsonProperty("dutytime8s")
    private String start8;

    @JsonProperty("dutytime1c")
    private String close1;

    @JsonProperty("dutytime2c")
    private String close2;

    @JsonProperty("dutytime3c")
    private String close3;

    @JsonProperty("dutytime4c")
    private String close4;

    @JsonProperty("dutytime5c")
    private String close5;

    @JsonProperty("dutytime6c")
    private String close6;

    @JsonProperty("dutytime7c")
    private String close7;

    @JsonProperty("dutytime8c")
    private String close8;

    @JsonProperty("@timestamp")
    @Transient
    private String timestamp;

    @JsonProperty("@version")
    @Transient
    private String version;

    @JsonProperty("location")
    @Transient
    private List<Double> latLon;

    @JsonProperty("postcdn1")
    @Transient
    private Long postcdn1;

    @JsonProperty("postcdn2")
    @Transient
    private Long postcdn2;

    @JsonProperty("dutyetc")
    private String dutyetc;

    public static PharmacyResponseDTO fromEntity(Pharmacy pharmacy) {
        return new PharmacyResponseDTO(
                pharmacy.getPhId(),
                pharmacy.getMaping(),
                pharmacy.getName(),
                pharmacy.getAddress(),
                pharmacy.getTel(),
                pharmacy.getLatitude(),
                pharmacy.getLongitude(),
                pharmacy.getTravelKm(),
                pharmacy.getTravelH(),
                pharmacy.getTravelM(),
                pharmacy.getTravelS(),
                pharmacy.getStart1(),
                pharmacy.getStart2(),
                pharmacy.getStart3(),
                pharmacy.getStart4(),
                pharmacy.getStart5(),
                pharmacy.getStart6(),
                pharmacy.getStart7(),
                pharmacy.getStart8(),
                pharmacy.getClose1(),
                pharmacy.getClose2(),
                pharmacy.getClose3(),
                pharmacy.getClose4(),
                pharmacy.getClose5(),
                pharmacy.getClose6(),
                pharmacy.getClose7(),
                pharmacy.getClose8(),
                pharmacy.getTimestamp(),
                pharmacy.getVersion(),
                pharmacy.getLatLon(),
                pharmacy.getPostcdn1(),
                pharmacy.getPostcdn2(),
                pharmacy.getDutyetc()
        );
    }
}
