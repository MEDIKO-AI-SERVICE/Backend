package com.mediko.mediko_server.domain.recommend.domain;

import jakarta.persistence.Column;

public class Geocode {
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private Double latitude ;

    @Column(name = "longitude", nullable = false)
    private Double longitude ;
}
