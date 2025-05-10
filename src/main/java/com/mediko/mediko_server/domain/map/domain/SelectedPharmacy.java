package com.mediko.mediko_server.domain.map.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "selected_ph")
public class SelectedPharmacy extends BaseEntity {

    @Column(name = "naver_map", nullable = false, length = 1000)
    private String naverMap;

    @Column(name = "kakao_map", nullable = false)
    private String kakaoMap;

    @Column(name = "google_map", nullable = false)
    private String googleMap;

    @ManyToOne
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;
}
