package com.mediko.mediko_server.domain.member.domain.repository;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasicInfoRepository extends JpaRepository<BasicInfo, Long> {

    // Member를 기반으로 BasicInfo 조회
    Optional<BasicInfo> findByMember(Member member);

    // Member를 기반으로 BasicInfo 존재 여부 확인
    boolean existsByMember(Member member);
}