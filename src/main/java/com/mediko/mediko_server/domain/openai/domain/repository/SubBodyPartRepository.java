package com.mediko.mediko_server.domain.openai.domain.repository;

import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SubBodyPartRepository extends JpaRepository<SubBodyPart, Long> {

    Optional<SubBodyPart> findByDescription(String description);

    List<SubBodyPart> findByMainBodyPartId(Long mainBodyPartId);
}
