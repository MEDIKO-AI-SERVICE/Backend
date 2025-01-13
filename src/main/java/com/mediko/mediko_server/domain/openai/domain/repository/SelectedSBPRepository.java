package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SelectedSBPRepository extends JpaRepository<SelectedSBP, Long> {
    // 최신 SelectedSBP를 조회하는 쿼리
    @Query(value = "SELECT * FROM selected_sbp WHERE member_id = :memberId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<SelectedSBP> findLatestByMemberId(@Param("memberId") Long memberId);
}


