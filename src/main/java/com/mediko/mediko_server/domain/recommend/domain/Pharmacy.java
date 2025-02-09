package com.mediko.mediko_server.domain.recommend.domain;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Location;
import com.mediko.mediko_server.domain.member.domain.Member;
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
@Table(name= "pharmacy")
public class Pharmacy extends BaseEntity {

    @Column(name = "ph_id", nullable = false)
    private Long phId;

    @Column(name = "ph_maping")
    private String maping;

    @Column(name = "ph_name")
    private String name;

    @Column(name = "ph_address")
    private String address;

    @Column(name = "ph_tel")
    private String tel;

    @Column(name = "hpLatitude")
    private Double latitude;

    @Column(name = "hpLongitude")
    private Double longitude;

    @Column(name = "travle_km")
    private Double travelKm;

    @Column(name = "travel_h")
    private Integer travelH;

    @Column(name = "travel_m")
    private Integer travelM;

    @Column(name = "trevel_s")
    private Integer travelS;

    @Column(name = "1s")
    private String start1;

    @Column(name = "2s")
    private String start2;

    @Column(name = "3s")
    private String start3;

    @Column(name = "4s")
    private String start4;

    @Column(name = "s")
    private String start5;

    @Column(name = "6s")
    private String start6;

    @Column(name = "7s")
    private String start7;

    @Column(name = "8s")
    private String start8;

    @Column(name = "1c")
    private String close1;

    @Column(name = "2c")
    private String close2;

    @Column(name = "3c")
    private String close3;

    @Column(name = "4c")
    private String close4;

    @Column(name = "5c")
    private String close5;

    @Column(name = "6c")
    private String close6;

    @Column(name = "7c")
    private String close7;

    @Column(name = "8c")
    private String close8;

    @Transient
    private String timestamp;

    @Transient
    private String version;

    @Transient
    private List<Double> latLon;

    @Transient
    private Long postcdn1;

    @Transient
    private Long postcdn2;

    @Transient
    private String dutyetc;

    @Column(name = "u_latitude")
    private Double userLatitude;

    @Column(name = "u_longitude")
    private Double userLongitude;

    @ManyToOne
    @JoinColumn(name = "basic_info_id", nullable = false)
    private BasicInfo basicInfo;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public List<Double> getLatLon() {
        return Arrays.asList(longitude, latitude);
    }
}
