package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SelectedMBPRepository extends JpaRepository<SelectedMBP, Long> {
    //사용자의 가장 최근에 생성된 SelectedMBP를 반환
    @Query(value = "SELECT * FROM selected_mbp s WHERE s.member_id = :memberId ORDER BY s.created_at DESC LIMIT 1", nativeQuery = true)
    Optional<SelectedMBP> findTopByMemberOrderByCreatedAtDesc(@Param("memberId") Long memberId);
}
