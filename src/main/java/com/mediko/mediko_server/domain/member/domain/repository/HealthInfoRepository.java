package com.mediko.mediko_server.domain.member.domain.repository;

import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthInfoRepository extends JpaRepository<HealthInfo, Long> {
    Optional<HealthInfo> findByMember(Member member);
    boolean existsByMember(Member member);
}
