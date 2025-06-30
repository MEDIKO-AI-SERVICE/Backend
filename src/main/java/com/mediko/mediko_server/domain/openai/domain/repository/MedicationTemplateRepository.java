package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.MedicationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationTemplateRepository extends JpaRepository<MedicationTemplate, Long> {
}
