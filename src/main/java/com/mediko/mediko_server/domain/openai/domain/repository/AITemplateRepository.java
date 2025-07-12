package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.AITemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AITemplateRepository extends JpaRepository<AITemplate, Long> {
    
    // 사용자의 모든 사전문진을 최신순으로 조회
    @Query("SELECT a FROM AITemplate a WHERE a.member = :member ORDER BY a.created_at DESC")
    List<AITemplate> findByMemberOrderByCreatedAtDesc(@Param("member") Member member);
    
    // 사용자의 최신 사전문진 3개 조회
    @Query("SELECT a FROM AITemplate a WHERE a.member = :member ORDER BY a.created_at DESC")
    List<AITemplate> findTop3ByMemberOrderByCreatedAtDesc(@Param("member") Member member, Pageable pageable);
}
