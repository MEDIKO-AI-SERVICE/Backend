package com.mediko.mediko_server.domain.recommend.domain.repository;

import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
}
