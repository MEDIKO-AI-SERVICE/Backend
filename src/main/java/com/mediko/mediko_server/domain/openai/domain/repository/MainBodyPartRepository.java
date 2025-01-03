package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MainBodyPartRepository extends JpaRepository<MainBodyPart, Long> {
    Optional<MainBodyPart> findByBody(String body);
}