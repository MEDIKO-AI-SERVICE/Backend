package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SymptomRepository  extends JpaRepository<Symptom, Long> {

    Optional<Symptom> findByIdAndMemberId(Long symptomId, Long memberId);
}
