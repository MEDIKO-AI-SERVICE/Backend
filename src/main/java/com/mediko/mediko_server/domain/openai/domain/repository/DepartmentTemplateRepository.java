package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.DepartmentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentTemplateRepository extends JpaRepository<DepartmentTemplate, Long> {
} 