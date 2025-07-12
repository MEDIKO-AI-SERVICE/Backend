package com.mediko.mediko_server.domain.member.domain.repository;

import com.mediko.mediko_server.domain.member.domain.TempMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempMemberRepository extends JpaRepository<TempMember, Long> {
    Optional<TempMember> findByIdAndIsUsedFalse(Long id);
} 