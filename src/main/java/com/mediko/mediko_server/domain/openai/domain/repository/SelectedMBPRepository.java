package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SelectedMBPRepository extends JpaRepository<SelectedMBP, Long> {

    Optional<SelectedMBP> findByIdAndMember(Long id, Member member);
}
