package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SelectedSBPRepository extends JpaRepository<SelectedSBP, Long> {
    Optional<SelectedSBP> findByIdAndMember(Long selectedSbpId, Member member);

    List<SelectedSBP> findBySelectedMBPAndMember(SelectedMBP selectedMBP, Member member);
}


