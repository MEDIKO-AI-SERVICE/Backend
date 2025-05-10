package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SelectedSBPRepository extends JpaRepository<SelectedSBP, Long> {

    @Query("SELECT s FROM SelectedSBP s WHERE s.id = :selectedSbpId AND s.selectedMBP.member = :member")
    Optional<SelectedSBP> findByIdAndMember(@Param("selectedSbpId") Long selectedSbpId, @Param("member") Member member);

    @Query("SELECT s FROM SelectedSBP s WHERE s.selectedMBP = :selectedMBP AND s.selectedMBP.member = :member")
    List<SelectedSBP> findBySelectedMBPAndMember(@Param("selectedMBP") SelectedMBP selectedMBP, @Param("member") Member member);
}


