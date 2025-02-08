package com.mediko.mediko_server.domain.recommend.domain;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Location;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.global.converter.LongListConverter;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;

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

    @Column(name = "is_condition")
    private Boolean isCondition;

    @Convert(converter = LongListConverter.class)
    private List<Long> conditions = new ArrayList<>();

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
}
