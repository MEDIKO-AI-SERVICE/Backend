package com.mediko.mediko_server.domain.recommend.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.report.domain.Report;
import com.mediko.mediko_server.global.converter.StringListConvert;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "hospital")
public class Hospital extends BaseEntity {

    @Column(name = "is_report", nullable = false)
    private boolean isReport;

    @Column(name = "u_department", nullable = false)
    private String userDepartment;

    @Convert(converter = StringListConvert.class)
    @Column(name = "disease", nullable = false)
    private List<String> suspectedDisease;

    @Column(name = "secondary_hp", nullable = false)
    private boolean secondaryHospital;

    @Column(name = "tertiary_hp", nullable = false)
    private boolean tertiaryHospital;

    @Column(name = "u_latitude")
    private Double userLatitude;

    @Column(name = "u_longitude")
    private Double userLongitude;

    @Column(name = "hp_name", nullable = false)
    private String name;

    @Column(name = "hp_telephone")
    private String telephone;

    @Column(name = "hp_department", nullable = false)
    private String hpDepartment;

    @Column(name = "hp_address")
    private String hpAddress;

    @Column(name = "hp_latitude")
    private Double hpLatitude;

    @Column(name = "hp_longitude")
    private Double hpLongitude;

    @Transient
    private List<Double> latLon;

    @Column(name = "sidocdnm")
    private String sidocdnm;

    @Column(name = "sggucdnm")
    private String sggucdnm;

    @Column(name = "emdongnm")
    private String emdongnm;

    @Column(name = "clcdnm")
    private String clcdnm;

    @Column(name = "es_distance_in_km")
    private Double esDistanceInKm;

    @Column(name = "travle_km")
    private Double travelKm;

    @Column(name = "travel_h")
    private Integer travelH;

    @Column(name = "travel_m")
    private Integer travelM;

    @Column(name = "trevel_s")
    private Integer travelS;

    @Column(name = "sort_score")
    private Long sortScore;

    @Column(name = "similarity")
    private Double similarity;

    @Column(name = "hp_url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public List<Double> getLatLon() {
        return Arrays.asList(hpLatitude, hpLongitude);
    }

}