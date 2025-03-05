package com.mediko.mediko_server.domain.report.domain.repository;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByIdAndMember(Long id, Member member);

    @Query("SELECT r FROM Report r WHERE r.member = :member ORDER BY r.created_at DESC")
    List<Report> findAllByMemberOrderByCreatedAtDesc(@Param("member") Member member);
}
