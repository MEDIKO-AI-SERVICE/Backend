package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import com.mediko.mediko_server.domain.openai.domain.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SymptomRepository  extends JpaRepository<Symptom, Long> {
    // Member와 관련된 symptom을 찾아주는 메서드
    Optional<Symptom> findByIdAndMemberId(Long symptomId, Long memberId);
}
