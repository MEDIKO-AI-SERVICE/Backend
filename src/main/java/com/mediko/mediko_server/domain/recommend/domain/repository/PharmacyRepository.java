package com.mediko.mediko_server.domain.recommend.domain.repository;

import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
}
