package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MainBodyPartRepository extends JpaRepository<MainBodyPart, Long> {
    // 주어진 body와 일치하는 MainBodyPart 조히
    Optional<MainBodyPart> findByBody(String body);
}