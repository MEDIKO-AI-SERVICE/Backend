package com.mediko.mediko_server.domain.recommend.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.global.converter.StringListConvert;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "er")
public class Er extends BaseEntity {

    @Column(name = "er_name", nullable = false)
    private String name;

    @Column(name = "er_address")
    private String address;

    @Column(name = "er_tel", nullable = false)
    private String tel;

    @Column(name = "hvamyn")
    private String hvamyn;

    @Column(name = "is_trauma")
    private Boolean isTrauma;

    @Column(name = "travel_km")
    private Double travelKm;

    @Column(name = "travel_h")
    private Integer travelH;

    @Column(name = "travel_m")
    private Integer travelM;

    @Column(name = "travel_s")
    private Integer travelS;

    @Convert(converter = StringListConvert.class)
    private List<String> conditions = new ArrayList<>();

    @Column(name = "u_latitude")
    private Double userLatitude;

    @Column(name = "u_longitude")
    private Double userLongitude;

    @Column(name = "er_latitude")
    private Double erLatitude;

    @Column(name = "er_longitude")
    private Double erLongitude;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
