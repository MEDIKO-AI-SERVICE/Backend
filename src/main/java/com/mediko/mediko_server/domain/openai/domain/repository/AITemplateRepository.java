package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.AITemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AITemplateRepository extends JpaRepository<AITemplate, Long> {
}
