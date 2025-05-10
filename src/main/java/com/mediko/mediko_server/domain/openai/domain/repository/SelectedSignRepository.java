package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.SelectedSign;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SelectedSignRepository extends JpaRepository<SelectedSign, Long> {

    @Query("SELECT s FROM SelectedSign s WHERE s.id = :id AND s.selectedSBP.selectedMBP.member = :member")
    Optional<SelectedSign> findByIdAndMember(@Param("id") Long id, @Param("member") Member member);
}
