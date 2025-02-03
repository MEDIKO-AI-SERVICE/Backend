package com.mediko.mediko_server.domain.member.domain;

import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "location")
public class Location extends BaseEntity {
    private Double lat;
    private Double lon;
}
