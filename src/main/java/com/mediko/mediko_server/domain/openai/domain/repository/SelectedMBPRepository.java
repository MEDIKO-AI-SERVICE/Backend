package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface SelectedMBPRepository extends JpaRepository<SelectedMBP, Long> {
    // 최신 SelectedMBP를 조회하는 쿼리
    @Query(value = "SELECT * FROM selected_mbp WHERE member_id = :memberId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<SelectedMBP> findLatestByMemberId(@Param("memberId") Long memberId);
}
