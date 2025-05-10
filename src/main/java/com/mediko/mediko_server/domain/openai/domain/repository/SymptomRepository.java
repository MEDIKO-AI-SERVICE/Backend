package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SymptomRepository  extends JpaRepository<Symptom, Long> {

    @Query("""
    SELECT s FROM Symptom s
    JOIN s.selectedSigns ss
    JOIN ss.selectedSBP sbp
    JOIN sbp.selectedMBP mbp
    WHERE s.id = :symptomId AND mbp.member.id = :memberId
""")
    Optional<Symptom> findByIdAndMemberId(@Param("symptomId") Long symptomId, @Param("memberId") Long memberId);
}
